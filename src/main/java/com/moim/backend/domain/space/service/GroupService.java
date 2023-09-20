package com.moim.backend.domain.space.service;

import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.groupvote.repository.VoteRepository;
import com.moim.backend.domain.hotplace.repository.HotPlaceRepository;
import com.moim.backend.domain.space.config.OdsayProperties;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.repository.BestPlaceRepository;
import com.moim.backend.domain.space.repository.GroupRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.space.request.GroupServiceRequest;
import com.moim.backend.domain.space.response.*;
import com.moim.backend.domain.subway.entity.Subway;
import com.moim.backend.domain.subway.repository.SubwayRepository;
import com.moim.backend.domain.subway.response.BestPlaceInterface;
import com.moim.backend.domain.user.config.KakaoProperties;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.global.common.Result;
import com.moim.backend.global.common.exception.CustomException;
import com.moim.backend.global.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.moim.backend.domain.space.response.GroupResponse.Participations.toParticipateEntity;
import static com.moim.backend.domain.space.response.GroupResponse.Region.toLocalEntity;
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
    private final OdsayProperties odsayProperties;
    private final KakaoProperties kakaoProperties;
    private final RestTemplate restTemplate = new RestTemplate();

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
        participateGroupValidate(request, user, group);

        // 어드민이 참여하는 경우 (즉, 모임이 생성된 직후)
        if (group.getAdminId().equals(user.getUserId())) {
            saveNearestStationList(request, group);
        }

        Participation participation = saveParticipation(request, user, group);

        return GroupResponse.Participate.response(participation);
    }

    private Participation saveParticipation(GroupServiceRequest.Participate request, Users user, Groups group) {
        return participationRepository.save(Participation.builder()
                .group(group)
                .userId(user.getUserId())
                .userName(request.getUserName())
                .locationName(request.getLocationName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .transportation(request.getTransportationType())
                .password(encrypt(request.getPassword()))
                .build());
    }

    private void participateGroupValidate(GroupServiceRequest.Participate request, Users user, Groups group) {
        validateLocationName(request.getLocationName());
        checkDuplicateParticipation(group, user);
        validateTransportation(request.getTransportationType());
    }

    private void saveNearestStationList(GroupServiceRequest.Participate request, Groups group) {
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

        // 모임장이 나가는 경우 스페이스 삭제
        if (group.getAdminId().equals(user.getUserId())) {
            groupRepository.delete(group);
            voteRepository.deleteByGroupId(group.getGroupId());
            return GroupResponse.Exit.response(true, "모임이 삭제되었습니다.");
        }

        participationRepository.delete(myParticipate);
        return GroupResponse.Exit.response(false, "모임에서 나갔습니다.");
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
        voteRepository.deleteByGroupId(groupId);
        return null;
    }

    // 모임 추천 지역 조회하기
    public List<PlaceRouteResponse> getBestRegion(Long groupId) {
        Instant start = Instant.now();
        Groups group = getGroup(groupId);
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

        // 경로 찾기
        List<PlaceRouteResponse> placeRouteResponseList = bestPlaceList.stream().map(bestPlace ->
                new PlaceRouteResponse(bestPlace, getMoveUserInfoList(bestPlace, group, participationList))
        ).collect(Collectors.toList());

        Instant end = Instant.now();
        log.info("[ 추천 지역 조회 시간: {}ms ] ========================================", Duration.between(start, end).toMillis());
        return placeRouteResponseList;
    }

    // 내 모임 확인하기
    public List<GroupResponse.MyParticipate> getMyParticipate(Users user) {
        List<Groups> groups = groupRepository.findByGroupsFetch(user.getUserId());

        return groups.stream()
                .map(GroupService::toMyParticiPateResponse)
                .toList();
    }

    private static GroupResponse.MyParticipate toMyParticiPateResponse(Groups group) {
        return GroupResponse.MyParticipate.response(
                group,
                group.getParticipations().stream()
                        .filter(participation -> participation.getUserId().equals(group.getAdminId()))
                        .map(Participation::getUserName).findFirst().orElseThrow(
                                () -> new CustomException(NOT_FOUND_PARTICIPATE)
                        ),
                group.getBestPlaces().stream().map(BestPlace::getPlaceName).toList(),
                group.getParticipations().stream().map(Participation::getUserName).toList());
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
        Users admin = getUser(group.getAdminId());
        List<GroupResponse.Region> regions = new ArrayList<>();

        group.getParticipations().forEach(participation -> toRegionsResponse(regions, participation));

        return GroupResponse.Detail.response(group, admin, regions);
    }

    private void toRegionsResponse(List<GroupResponse.Region> regions, Participation participation) {
        // 그룹화 지역 이름 생성
        String regionName = getRegionName(participation);

        // 참여 정보 응답 객체 변환
        Users user = getUser(participation.getUserId());
        GroupResponse.Participations participateEntity = toParticipateEntity(participation, user);

        // 생성된 그룹화 지역 이름과 일치하는 그룹화 지역이 이미 존재하는지 Optional 검증
        Optional<GroupResponse.Region> optionalRegion = findRegionByName(regions, regionName);

        // 검증에 맞추어 Region 업데이트
        toUpdateRegion(regions, regionName, participateEntity, optionalRegion);
    }

    private static void toUpdateRegion(List<GroupResponse.Region> regions, String regionName, GroupResponse.Participations participateEntity, Optional<GroupResponse.Region> optionalRegion) {
        if (optionalRegion.isEmpty()) { // 존재하지 않는다면
            addNewRegion(regions, regionName, participateEntity);
        } else { // 존재한다면
            addParticipationToExistingRegion(participateEntity, optionalRegion.get());
        }
    }

    private static void addParticipationToExistingRegion(GroupResponse.Participations participateEntity, GroupResponse.Region region) {
        List<GroupResponse.Participations> participations = new ArrayList<>(region.getParticipations());
        participations.add(participateEntity);
        region.setParticipations(participations);
    }

    private static void addNewRegion(List<GroupResponse.Region> regions, String regionName, GroupResponse.Participations participateEntity) {
        regions.add(toLocalEntity(regionName, participateEntity));
    }

    private static Optional<GroupResponse.Region> findRegionByName(List<GroupResponse.Region> regions, String regionName) {
        return regions.stream()
                .filter(local -> local.getRegionName().equals(regionName))
                .findFirst();
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

    private static Function<NaverMapListDto.placeList, GroupResponse.Place> toPlaceEntity(Double x, Double y, String local) {
        return naver -> {
            GroupResponse.Place response = GroupResponse.Place.response(naver, local);
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

    private Groups toGroupEntity(GroupServiceRequest.Create request, Users user) {
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
            BestPlaceInterface bestPlace,
            Groups group,
            List<Participation> participationList
    ) {
        List<PlaceRouteResponse.MoveUserInfo> moveUserInfoList = new ArrayList<>();

        participationList.forEach(participation -> {
            if ((participation.getTransportation() == TransportationType.PUBLIC)) {
                getBusRouteToResponse(bestPlace, group, participation)
                        .ifPresent(moveUserInfo -> moveUserInfoList.add(moveUserInfo));
            } else if (participation.getTransportation() == TransportationType.PERSONAL) {
                getCarRouteToResponse(bestPlace, group, participation)
                        .ifPresent(moveUserInfo -> moveUserInfoList.add(moveUserInfo));
            }
        });

        return moveUserInfoList;
    }

    private Optional<PlaceRouteResponse.MoveUserInfo> getBusRouteToResponse(
            BestPlaceInterface bestPlace, Groups group, Participation participation
    ) {
        Instant start = Instant.now();

        Optional<PlaceRouteResponse.MoveUserInfo> moveUserInfo = Optional.empty();
        BusPathResponse busPathResponse = restTemplate.getForObject(
                odsayProperties.getSearchPathUriWithParams(bestPlace, participation),
                BusPathResponse.class
        );

        if (busPathResponse.getResult() == null) {
            log.debug("[ 버스 길찾기 실패 ] ========================================");
            log.debug("지역: {}, url: {}", bestPlace.getName(), odsayProperties.getSearchPathUriWithParams(bestPlace, participation));
        } else {
            log.debug("[ 버스 길찾기 성공 ] ========================================");
            log.debug("지역: {}, url: {}", bestPlace.getName(), odsayProperties.getSearchPathUriWithParams(bestPlace, participation));

            BusGraphicDataResponse busGraphicDataResponse = restTemplate.getForObject(
                    odsayProperties.getGraphicDataUriWIthParams(busPathResponse.getPathInfoMapObj()),
                    BusGraphicDataResponse.class
            );
            if (busPathResponse.getResult() == null) {
                log.debug("[ 버스 그래픽 데이터 조회 실패 ] ========================================");
                log.debug("지역: {}, url: {}", bestPlace.getName(), odsayProperties.getSearchPathUriWithParams(bestPlace, participation));
            } else {
                log.debug("[ 버스 그래픽 데이터 조회 성공 ] ========================================");
                log.debug("지역: {}, url: {}", bestPlace.getName(), odsayProperties.getSearchPathUriWithParams(bestPlace, participation));

                moveUserInfo = Optional.of(new PlaceRouteResponse.MoveUserInfo(group, participation, busGraphicDataResponse, busPathResponse));
            }
        }

        Instant end = Instant.now();
        log.info("[ 버스 경로 조회 시간: {}ms ] ========================================", Duration.between(start, end).toMillis());
        return moveUserInfo;
    }

    private Optional<PlaceRouteResponse.MoveUserInfo> getCarRouteToResponse(
            BestPlaceInterface bestPlace, Groups group, Participation participation
    ) {
        Instant start = Instant.now();

        Optional<PlaceRouteResponse.MoveUserInfo> moveUserInfo = Optional.empty();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoProperties.getClientId());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        CarMoveInfo carMoveInfo = restTemplate.exchange(
                kakaoProperties.getSearchCarPathUriWithParams(bestPlace, participation),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                CarMoveInfo.class
        ).getBody();
        if (carMoveInfo.getRoutes() == null) {
            log.debug("[ 차 길찾기 조회 실패 ] ========================================");
            log.debug("지역: {}, url: {}", bestPlace.getName(), kakaoProperties.getSearchCarPathUriWithParams(bestPlace, participation));
        } else {
            log.debug("[ 차 길찾기 조회 성공 ] ========================================");
            log.debug("지역: {}, url: {}", bestPlace.getName(), kakaoProperties.getSearchCarPathUriWithParams(bestPlace, participation));

            moveUserInfo = Optional.of(new PlaceRouteResponse.MoveUserInfo(group, participation, carMoveInfo));
        }

        Instant end = Instant.now();
        log.info("[ 차 경로 조회 시간: {}ms ] ========================================", Duration.between(start, end).toMillis());
        return moveUserInfo;
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
}
