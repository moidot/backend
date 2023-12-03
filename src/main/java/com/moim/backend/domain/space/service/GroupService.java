package com.moim.backend.domain.space.service;

import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.groupvote.repository.VoteRepository;
import com.moim.backend.domain.hotplace.repository.HotPlaceRepository;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.repository.BestPlaceRepository;
import com.moim.backend.domain.space.repository.GroupRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.space.request.service.GroupCreateServiceRequest;
import com.moim.backend.domain.space.request.service.GroupNameUpdateServiceRequest;
import com.moim.backend.domain.space.request.service.GroupParticipateServiceRequest;
import com.moim.backend.domain.space.request.service.GroupParticipateUpdateServiceRequest;
import com.moim.backend.domain.space.response.*;
import com.moim.backend.domain.space.response.group.*;
import com.moim.backend.domain.subway.entity.Subway;
import com.moim.backend.domain.subway.repository.SubwayRepository;
import com.moim.backend.domain.subway.response.BestPlaceInterface;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.global.aspect.TimeCheck;
import com.moim.backend.global.common.CacheName;
import com.moim.backend.global.common.Result;
import com.moim.backend.global.common.exception.CustomException;
import com.moim.backend.global.util.DistanceCalculator;
import com.moim.backend.global.util.RedisDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
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
import java.util.stream.Collectors;

import static com.moim.backend.domain.space.response.group.GroupParticipationsResponse.toParticipateEntity;
import static com.moim.backend.domain.space.response.group.GroupRegionResponse.toLocalEntity;
import static com.moim.backend.global.common.Result.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final GroupRepository groupRepository;
    private final ParticipationRepository participationRepository;
    private final BestPlaceRepository bestPlaceRepository;
    private final SubwayRepository subwayRepository;
    private final HotPlaceRepository hotPlaceRepository;
    private final DirectionService directionService;
    private final RedisDao redisDao;
    private final RestTemplate restTemplate = new RestTemplate();

    // 모임 생성
    @Transactional
    public GroupCreateResponse createGroup(GroupCreateServiceRequest request, Users user) {
        Groups group = groupRepository.save(toGroupEntity(request, user));
        saveParticipation(
                user, group, request.getUserName(),
                request.getLocationName(), request.getLatitude(), request.getLongitude(),
                request.getTransportationType(), request.getPassword()
        );

        saveNearestStationList(group, request.getLatitude(), request.getLongitude());

        return GroupCreateResponse.response(group);
    }

    private void saveNearestStationList(Groups group, Double latitude, Double longitude) {
        List<Subway> nearestStationsList =
                subwayRepository.getNearestStationsList(latitude, longitude);

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

    // 모임 참여
    @Transactional
    @CacheEvict(value = CacheName.group, key = "#request.getGroupId()")
    public GroupParticipateResponse participateGroup(GroupParticipateServiceRequest request, Users user) {
        Groups group = getGroup(request.getGroupId());

        participateGroupValidate(request, user, group);
        Participation participation = saveParticipation(
                user, group, request.getUserName(),
                request.getLocationName(), request.getLatitude(), request.getLongitude(),
                request.getTransportationType(), request.getPassword()
        );

        bestPlaceRepository.deleteAllInBatch(bestPlaceRepository.findAllByGroup(group));

        for (BestPlaceInterface bestPlace : calculateBestPlaces(group)) {
            bestPlaceRepository.save(
                    BestPlace.builder()
                            .group(group)
                            .placeName(bestPlace.getName())
                            .latitude(bestPlace.getLatitude())
                            .longitude(bestPlace.getLongitude())
                            .build()
            );
        }

        return GroupParticipateResponse.response(participation);
    }

    private Participation saveParticipation(
            Users user, Groups group, String userName,
            String locationName, Double latitude, Double longitude,
            TransportationType transportation, String password
    ) {
        return participationRepository.save(Participation.builder()
                .group(group)
                .userId(user.getUserId())
                .userName(userName)
                .locationName(locationName)
                .latitude(latitude)
                .longitude(longitude)
                .transportation(transportation)
                .password(encrypt(password))
                .build());
    }

    private void participateGroupValidate(GroupParticipateServiceRequest request, Users user, Groups group) {
        validateLocationName(request.getLocationName());
        checkDuplicateParticipation(group, user);
        validateTransportation(request.getTransportationType());
    }

    // 내 참여 정보 수정
    @Transactional
    public GroupParticipateUpdateResponse participateUpdate(
            GroupParticipateUpdateServiceRequest request, Users user
    ) {
        Participation myParticipate = getParticipate(request.getParticipateId());
        validateParticipationMyInfo(user, myParticipate);
        myParticipate.update(request);
        return GroupParticipateUpdateResponse.response(myParticipate);
    }

    // 내 모임 나가기
    @Transactional
    public GroupExitResponse participateExit(Long participateId, Users user) {
        Participation myParticipate = getParticipate(participateId);
        validateParticipationMyInfo(user, myParticipate);

        Groups group = myParticipate.getGroup();
        Optional<Vote> optionalVote = voteRepository.findByGroupId(group.getGroupId());

        // 투표 시작시 모임 나가기 불가
        checkIfVoteStartedBeforeLeaving(optionalVote);

        // 관련 Redis 삭제
        redisDao.deleteSpringCache(CacheName.group, group.getGroupId().toString());

        // 모임장이 나가는 경우 스페이스 삭제
        if (deleteGroupIfAdminLeaves(user, group)) {
            return GroupExitResponse.response(true, "모임이 삭제되었습니다.");
        }

        participationRepository.delete(myParticipate);
        return GroupExitResponse.response(false, "모임에서 나갔습니다.");
    }

    private boolean deleteGroupIfAdminLeaves(Users user, Groups group) {
        if (group.getAdminId().equals(user.getUserId())) {
            groupRepository.delete(group);
            voteRepository.deleteByGroupId(group.getGroupId());
            return true;
        }
        return false;
    }

    private static void checkIfVoteStartedBeforeLeaving(Optional<Vote> optionalVote) {
        if (optionalVote.isPresent() && optionalVote.get().getIsClosed().equals(false)) {
            throw new CustomException(Result.ALREADY_CREATED_VOTE);
        }
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
    @CacheEvict(value = CacheName.group, key = "#groupId")
    public Void participateDelete(Long groupId, Users user) {
        Groups group = getGroup(groupId);
        validateAdminStatus(user.getUserId(), group.getAdminId());
        groupRepository.delete(group);
        voteRepository.deleteByGroupId(groupId);
        return null;
    }

    // 모임 추천 지역 조회하기
    @TimeCheck
    @Cacheable(value = CacheName.group, key = "#groupId")
    public List<PlaceRouteResponse> getBestRegion(Long groupId) {
        Groups group = getGroup(groupId);
        List<Participation> participationList = participationRepository.findAllByGroup(group);

        // 추천 지역 조회 + 해당 지역으로 이동하는 경로 계산
        return bestPlaceRepository.findAllByGroup(group).stream().map(bestPlace -> new PlaceRouteResponse(
                bestPlace, getMoveUserInfoList(bestPlace, group, participationList)
        )).collect(Collectors.toList());
    }

    // 내 모임 확인하기
    public List<GroupMyParticipateResponse> getMyParticipate(Users user) {
        List<Groups> groups = groupRepository.findByGroupsFetch(user.getUserId());

        return groups.stream()
                .map(GroupService::toMyParticiPateResponse)
                .toList();
    }

    private static GroupMyParticipateResponse toMyParticiPateResponse(Groups group) {
        return GroupMyParticipateResponse.response(
                group,
                getGroupAdminName(group),
                group.getBestPlaces().stream().map(BestPlace::getPlaceName).toList(),
                group.getParticipations().stream().map(Participation::getUserName).toList());
    }

    private static String getGroupAdminName(Groups group) {
        return group.getParticipations().stream()
                .filter(participation -> participation.getUserId().equals(group.getAdminId()))
                .map(Participation::getUserName).findFirst().orElseThrow(
                        () -> new CustomException(NOT_FOUND_PARTICIPATE)
                );
    }

    // 모임 장소 추천 조회 리스트 API
    public List<GroupPlaceResponse> keywordCentralizedMeetingSpot(Double x, Double y, String local, String keyword) {
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
    public GroupDetailResponse readParticipateGroupByRegion(Long groupId) {
        Groups group = getGroupByFetchParticipation(groupId);
        Users admin = getUser(group.getAdminId());
        List<GroupRegionResponse> regions = new ArrayList<>();

        group.getParticipations().forEach(participation -> toRegionsResponse(regions, participation));

        return GroupDetailResponse.response(group, admin, regions);
    }

    private void toRegionsResponse(List<GroupRegionResponse> regions, Participation participation) {
        // 그룹화 지역 이름 생성
        String regionName = getRegionName(participation);

        // 참여 정보 응답 객체 변환
        Users user = getUser(participation.getUserId());
        GroupParticipationsResponse participateEntity = toParticipateEntity(participation, user);

        // 생성된 그룹화 지역 이름과 일치하는 그룹화 지역이 이미 존재하는지 Optional 검증
        Optional<GroupRegionResponse> optionalRegion = findRegionByName(regions, regionName);

        // 검증에 맞추어 Region 업데이트
        toUpdateRegion(regions, regionName, participateEntity, optionalRegion);
    }

    private static void toUpdateRegion(List<GroupRegionResponse> regions, String regionName, GroupParticipationsResponse participateEntity, Optional<GroupRegionResponse> optionalRegion) {
        if (optionalRegion.isEmpty()) { // 존재하지 않는다면
            addNewRegion(regions, regionName, participateEntity);
        } else { // 존재한다면
            addParticipationToExistingRegion(participateEntity, optionalRegion.get());
        }
    }

    private static void addParticipationToExistingRegion(GroupParticipationsResponse participateEntity, GroupRegionResponse region) {
        List<GroupParticipationsResponse> participations = new ArrayList<>(region.getParticipations());
        participations.add(participateEntity);
        region.setParticipations(participations);
    }

    private static void addNewRegion(List<GroupRegionResponse> regions, String regionName, GroupParticipationsResponse participateEntity) {
        regions.add(toLocalEntity(regionName, participateEntity));
    }

    private static Optional<GroupRegionResponse> findRegionByName(List<GroupRegionResponse> regions, String regionName) {
        return regions.stream()
                .filter(local -> local.getRegionName().equals(regionName))
                .findFirst();
    }

    // 모임 이름 수정 API
    public Void updateGroupName(Long groupId, GroupNameUpdateServiceRequest request, Users user) {
        Groups group = getGroup(groupId);
        validateAdminStatus(user.getUserId(), group.getAdminId());
        group.updateGroupName(request.getGroupName());
        return null;
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

    private static void validateLocationName(String locationName) {
        String[] validateLocation = locationName.split(" ");
        if (validateLocation.length < 2) {
            throw new CustomException(INCORRECT_LOCATION_NAME);
        }
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
        if (!transportation.equals(TransportationType.PUBLIC) && !transportation.equals(TransportationType.PERSONAL)) {
            throw new CustomException(INVALID_TRANSPORTATION);
        }
    }

    private static void validateParticipationMyInfo(Users user, Participation myParticipate) {
        if (!myParticipate.getUserId().equals(user.getUserId())) {
            throw new CustomException(NOT_MATCHED_PARTICIPATE);
        }
    }


    // method

    private static String getRegionName(Participation participation) {
        validateLocationName(participation.getLocationName());
        StringTokenizer st = new StringTokenizer(participation.getLocationName());
        return String.format("%s %s", st.nextToken(), st.nextToken());
    }

    private static Function<NaverMapListDto.placeList, GroupPlaceResponse> toPlaceEntity(Double x, Double y, String local) {
        return naver -> {
            GroupPlaceResponse response = GroupPlaceResponse.response(naver, local);
            double placeX = Double.parseDouble(naver.getX());
            double placeY = Double.parseDouble(naver.getY());
            String distance = String.format("%s(으)로부터 %sm", local, (int) DistanceCalculator.getDistance(y, x, placeY, placeX));
            response.setDistance(distance);
            return response;
        };
    }

    private Groups getGroupByFetchParticipation(Long groupId) {
        return groupRepository.findByGroupParticipation(groupId).orElseThrow(
                () -> new CustomException(NOT_FOUND_GROUP)
        );
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

    private Groups toGroupEntity(GroupCreateServiceRequest request, Users user) {
        return Groups.builder()
                .adminId(user.getUserId())
                .name(request.getName())
                .date(request.getDate())
                .build();
    }

    private Participation getParticipate(Long id) {
        return participationRepository.findById(id).orElseThrow(
                () -> new CustomException(NOT_FOUND_PARTICIPATE)
        );
    }

    private List<PlaceRouteResponse.MoveUserInfo> getMoveUserInfoList(
            BestPlace bestPlace,
            Groups group,
            List<Participation> participationList
    ) {
        List<PlaceRouteResponse.MoveUserInfo> moveUserInfoList = new ArrayList<>();

        participationList.forEach(participation -> {
            if ((participation.getTransportation() == TransportationType.PUBLIC)) {
                directionService.getBusRouteToResponse(bestPlace, group, participation)
                        .ifPresent(moveUserInfo -> moveUserInfoList.add(moveUserInfo));
            } else if (participation.getTransportation() == TransportationType.PERSONAL) {
                directionService.getCarRouteToResponse(bestPlace, group, participation)
                        .ifPresent(moveUserInfo -> moveUserInfoList.add(moveUserInfo));
            }
        });

        return moveUserInfoList;
    }

    private double getValidRange(List<Participation> participationList, MiddlePoint middlePoint) {
        double maxDistance = participationList.stream().mapToDouble(participation ->
                DistanceCalculator.getDistance(
                        participation.getLatitude(),
                        participation.getLongitude(),
                        middlePoint.getLatitude(),
                        middlePoint.getLongitude()
                )
        ).max().getAsDouble();

        return maxDistance / 2;
    }


    private Users getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new CustomException(NOT_FOUND_PARTICIPATE)
        );
    }

    private List<BestPlaceInterface> calculateBestPlaces(Groups group) {
        // 중간 좌표 구하기
        MiddlePoint middlePoint = participationRepository.getMiddlePoint(group);

        // 유효범위 찾기
        List<Participation> participationList = participationRepository.findAllByGroup(group);
        double validRange = getValidRange(participationList, middlePoint);

        // 근처 지하철역 찾기
        List<BestPlaceInterface> bestPlaceList = subwayRepository.getBestSubwayList(
                middlePoint.getLatitude(), middlePoint.getLongitude(), validRange
        );

        // 근처에 지하철역이 없다면, 인기 장소 찾기
        if (bestPlaceList.isEmpty()) {
            bestPlaceList = hotPlaceRepository.getBestHotPlaceList(
                    middlePoint.getLatitude(), middlePoint.getLongitude(), validRange
            );
        }

        return bestPlaceList;
    }
}
