package com.moim.backend.domain.space.service;

import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.groupvote.repository.VoteRepository;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.repository.BestPlaceRepository;
import com.moim.backend.domain.space.repository.GroupRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.space.request.GroupServiceRequest;
import com.moim.backend.domain.space.response.GroupResponse;
import com.moim.backend.domain.space.response.MiddlePoint;
import com.moim.backend.domain.space.response.NaverMapListDto;
import com.moim.backend.domain.subway.entity.Subway;
import com.moim.backend.domain.subway.repository.SubwayRepository;
import com.moim.backend.domain.subway.response.BestSubwayInterface;
import com.moim.backend.domain.user.config.KakaoProperties;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.common.Result;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.function.Function;

import static com.moim.backend.domain.space.entity.TransportationType.PERSONAL;
import static com.moim.backend.domain.space.entity.TransportationType.PUBLIC;
import static com.moim.backend.domain.space.response.GroupResponse.Region.toLocalEntity;
import static com.moim.backend.domain.space.response.GroupResponse.Participations.toParticipateEntity;
import static com.moim.backend.global.common.Result.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {

    private final KakaoProperties kakaoProperties;
    private final RestTemplate restTemplate = new RestTemplate();
    private final VoteRepository voteRepository;
    private final GroupRepository groupRepository;
    private final ParticipationRepository participationRepository;
    private final BestPlaceRepository bestPlaceRepository;
    private final SubwayRepository subwayRepository;

    // 모임 생성
    @Transactional
    public GroupResponse.Create createGroup(GroupServiceRequest.Create request, Users user) {
        Groups group = groupRepository.save(toGroupEntity(request, user));

        return GroupResponse.Create.response(group);
    }

    // 모임 참여
    @Transactional
    public GroupResponse.Participate participateGroup(GroupServiceRequest.Participate request, Users user) {
        Groups group = getGroup(request.getGroupId());

        checkDuplicateParticipation(group, user);
        validateTransportation(request.getTransportationType());

        // 어드민이 참여하는 경우 (즉, 모임이 생성된 직후)
        if (group.getAdminId().equals(user.getUserId())) {
            List<Subway> nearestStationsList =
                    subwayRepository.getNearestStationsList(request.getLatitude(), request.getLongitude());

            for (Subway subway : nearestStationsList) {
                bestPlaceRepository.save(
                        BestPlace.builder()
                                .group(group)
                                .placeName(subway.getName())
                                .latitude(subway.getLatitude().doubleValue())
                                .longitude(subway.getLongitude().doubleValue())
                                .build()
                );
            }
        }

        String encryptedPassword = encrypt(request.getPassword());
        Participation participation = participationRepository.save(
                toParticipationEntity(request, group, user.getUserId(), encryptedPassword)
        );

        return GroupResponse.Participate.response(participation);
    }

    // 내 참여 정보 수정
    @Transactional
    public GroupResponse.ParticipateUpdate participateUpdate(
            GroupServiceRequest.ParticipateUpdate request, Users user
    ) {
        Participation myParticipate = getParticipate(request.getParticipateId());
        validateParticipationMyInfo(user, myParticipate);
        myParticipate.update(request);
        return GroupResponse.ParticipateUpdate.response(myParticipate);
    }

    // 내 모임 나가기
    @Transactional
    public GroupResponse.Exit participateExit(Long participateId, Users user) {
        Participation myParticipate = getParticipate(participateId);
        validateParticipationMyInfo(user, myParticipate);
        // TODO : fetchJoin 적용여부 판단
        Groups group = myParticipate.getGroup();
        Optional<Vote> optionalVote = voteRepository.findByGroupId(group.getGroupId());

        // 투표 시작시 모임 나가기 불가
        if (optionalVote.isPresent() && optionalVote.get().getIsClosed().equals(true)) {
            throw new CustomException(Result.ALREADY_CREATED_VOTE);
        }

        // TODO : 추후 투표도 같이 삭제해야함
        // 모임장이 나가는 경우 스페이스 삭제
        if (group.getAdminId().equals(user.getUserId())) {
            groupRepository.delete(group);
            return GroupResponse.Exit.response(true, "모임이 삭제되었습니다.");
        }

        participationRepository.delete(myParticipate);
        return GroupResponse.Exit.response(false, "모임에서 나갔습니다.");
    }

    private Vote getVote(Groups group) {
        return voteRepository.findByGroupId(group.getGroupId()).orElseThrow(
                () -> new CustomException(NOT_CREATED_VOTE)
        );
    }


    // 모임원 내보내기
    @Transactional
    public Void participateRemoval(Long participateId, Users user) {
        Participation participate = getParticipate(participateId);
        Groups group = participate.getGroup();
        validateAdminStatus(user.getUserId(), group.getAdminId());
        participationRepository.delete(participate);
        return null;
    }

    // 모임 삭제
    @Transactional
    public Void participateDelete(Long groupId, Users user) {
        Groups group = getGroup(groupId);
        validateAdminStatus(user.getUserId(), group.getAdminId());
        groupRepository.delete(group);
        return null;
    }

    // 모임 추천 지역 조회하기
    public List<BestSubwayInterface> getBestRegion(Long groupId) {
        Groups group = getGroup(groupId);
        MiddlePoint middlePoint = participationRepository.getMiddlePoint(group);
        List<BestSubwayInterface> bestSubwayList = subwayRepository.getBestSubwayList(
                middlePoint.getLatitude(), middlePoint.getLongitude()
        );
        // TODO : 이름이 같은 역일 경우 중간 지점에 더 가까운 역으로 조회(DB 또는 Application 계층 중 어디에서 처리해야할지 고민)
        // TODO : 조회한 역이 적절하지 않은 경우 인기 지역 조회

        return bestSubwayList;
    }

    // 내 모임 확인하기
    public List<GroupResponse.MyParticipate> getMyParticipate(Users user) {
        List<Groups> groups = groupRepository.myParticipationGroups(user.getUserId());
        return groups.stream()
                .map(group -> GroupResponse.MyParticipate.response(
                        group, groupBestPlaceToList(group))
                ).toList();
    }

    // 모임 장소 추천 조회 리스트 API
    public List<GroupResponse.Place> keywordCentralizedMeetingSpot(Double x, Double y, String local, String keyword) {
        // 네이버 API 요청
        URI uri = createNaverRequestUri(local, keyword);
        ResponseEntity<NaverMapListDto> naverResponse = restTemplate.getForEntity(uri.toString(), NaverMapListDto.class);

        // 응답 처리
        Optional<NaverMapListDto> optionalBody = Optional.ofNullable(naverResponse.getBody());
        if (!naverResponse.getStatusCode().is2xxSuccessful() || optionalBody.isEmpty()) {
            throw new CustomException(NOT_REQUEST_NAVER);
        }

        // 장소 정보 가져오기
        List<NaverMapListDto.placeList> placeList = optionalBody.get().getResult().getPlace().getList();

        return placeList.stream().map(toPlaceEntity(x, y, local)).toList();
    }

    // 모임 참여자 정보 리스트 조회 API
    public GroupResponse.Detail readParticipateGroupByRegion(Long groupId) {
        Groups group = getGroupByFetchParticipation(groupId);
        List<GroupResponse.Region> regions = getParticipantsByRegion(group);

        return GroupResponse.Detail.response(group, regions);
    }


    // validate

    private static URI createNaverRequestUri(String local, String keyword) {
        return UriComponentsBuilder.fromHttpUrl("https://map.naver.com/v5/api/search")
                .queryParam("caller", "pcweb")
                .queryParam("query", local + keyword)
                .queryParam("type", "all")
                .queryParam("page", 1)
                .queryParam("displayCount", 12)
                .queryParam("isPlaceRecommendationReplace", true)
                .queryParam("lang", "ko")
                .build()
                .toUri();
    }
    private void checkDuplicateParticipation(Groups group, Users user) {
        if (participationRepository.countByGroupAndUserId(group, user.getUserId()) > 0) {
            throw new CustomException(DUPLICATE_PARTICIPATION);
        }
    }

    private static void validateAdminStatus(Long userId, Long adminId) {
        if (!adminId.equals(userId)) {
            throw new CustomException(NOT_ADMIN_USER);
        }
    }

    private static void validateTransportation(TransportationType transportation) {
        if (!transportation.equals(PUBLIC) && !transportation.equals(PERSONAL)) {
            throw new CustomException(INVALID_TRANSPORTATION);
        }
    }

    private static void validateParticipationMyInfo(Users user, Participation myParticipate) {
        if (!myParticipate.getUserId().equals(user.getUserId())) {
            throw new CustomException(NOT_MATCHED_PARTICIPATE);
        }
    }



    // method
    private static List<GroupResponse.Region> getParticipantsByRegion(Groups group) {
        List<GroupResponse.Region> regions = new ArrayList<>();

        group.getParticipations().forEach(participation -> {
            // 내 그룹화 지역 이름 생성
            StringTokenizer st = new StringTokenizer(participation.getLocationName());
            String regionName = String.format("%s %s", st.nextToken(), st.nextToken());

            // 내 참여 정보 응답 객체 변환
            GroupResponse.Participations participateEntity = toParticipateEntity(participation);

            // 내 그룹화 지역 이름과 일치하는 그룹화 지역이 이미 존재하는지 확인
            Optional<GroupResponse.Region> optionalRegion = regions.stream()
                    .filter(local -> local.getRegionName().equals(regionName))
                    .findFirst();

            // 존재하지 않는다면
            if (optionalRegion.isEmpty()) {
                // 내 그룹화 지역 등록 및 해당 그룹화 참여자로 등록
                regions.add(toLocalEntity(regionName, participateEntity));
            }
            // 존재한다면
            else {
                GroupResponse.Region region = optionalRegion.get();
                List<GroupResponse.Participations> participations = new ArrayList<>(region.getParticipations());
                participations.add(participateEntity);
                // 그룹화 되어있는 지역에 내 참여정보 등록
                region.setParticipations(participations);
            }
        });

        return regions;
    }

    private static Function<NaverMapListDto.placeList, GroupResponse.Place> toPlaceEntity(Double x, Double y, String local) {
        return naver -> {
            GroupResponse.Place response = GroupResponse.Place.response(naver, local);
            double placeX = Double.parseDouble(naver.getX());
            double placeY = Double.parseDouble(naver.getY());
            String distance = String.format("%s(으)로부터 %sm", local, (int) calculateDistance(y, x, placeY, placeX, "meter"));
            response.setDistance(distance);
            return response;
        };
    }

    private Groups getGroupByFetchParticipation(Long groupId) {
        return groupRepository.findByGroupParticipation(groupId).orElseThrow(
                () -> new CustomException(NOT_FOUND_GROUP)
        );
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit.equals("kilometer")) {
            dist = dist * 1.609344;
        } else if (unit.equals("meter")) {
            dist = dist * 1609.344;
        }

        return (dist);
    }


    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private Groups getGroup(Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new CustomException(NOT_FOUND_GROUP));
    }

    public static String encrypt(String password) {
        if (password == null) return null;
        else {
            try {
                StringBuilder sb = new StringBuilder();
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(password.getBytes());
                byte[] bytes = md.digest();
                for (byte aByte : bytes) {
                    sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new CustomException(FAIL);
            }
        }
    }

    private Groups toGroupEntity(GroupServiceRequest.Create request, Users user) {
        return Groups.builder()
                .adminId(user.getUserId())
                .name(request.getName())
                .date(request.getDate())
                .build();
    }

    private Participation toParticipationEntity(
            GroupServiceRequest.Participate request, Groups group, long userId, String encryptedPassword
    ) {
        return Participation.builder()
                .group(group)
                .userId(userId)
                .userName(request.getUserName())
                .locationName(request.getLocationName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .transportation(request.getTransportationType())
                .password(encryptedPassword)
                .build();
    }

    private Participation getParticipate(Long id) {
        return participationRepository.findById(id).orElseThrow(
                () -> new CustomException(NOT_FOUND_PARTICIPATE)
        );
    }

    private static List<GroupResponse.BestPlaces> groupBestPlaceToList(Groups group) {
        // 로직상 NPE 가 발생할 수 없음
        List<BestPlace> bestPlaces = group.getBestPlaces();
        return bestPlaces.stream()
                .map(GroupResponse.BestPlaces::response)
                .toList();
    }
}
