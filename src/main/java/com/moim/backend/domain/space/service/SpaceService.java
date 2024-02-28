package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.request.SpaceCreateRequest;
import com.moim.backend.domain.space.request.SpaceNameUpdateRequest;
import com.moim.backend.domain.space.request.SpaceParticipateRequest;
import com.moim.backend.domain.space.request.SpaceParticipateUpdateRequest;
import com.moim.backend.domain.spacevote.entity.Vote;
import com.moim.backend.domain.spacevote.repository.VoteRepository;
import com.moim.backend.domain.hotplace.repository.HotPlaceRepository;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.repository.BestPlaceRepository;
import com.moim.backend.domain.space.repository.SpaceRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.space.response.MiddlePoint;
import com.moim.backend.domain.space.response.NaverMapListDto;
import com.moim.backend.domain.space.response.NicknameValidationResponse;
import com.moim.backend.domain.space.response.PlaceRouteResponse;
import com.moim.backend.domain.space.response.space.*;
import com.moim.backend.domain.subway.repository.SubwayRepository;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.global.aspect.TimeCheck;
import com.moim.backend.global.common.CacheName;
import com.moim.backend.global.common.Result;
import com.moim.backend.global.common.exception.CustomException;
import com.moim.backend.global.dto.BestRegion;
import com.moim.backend.global.util.DistanceCalculator;
import com.moim.backend.global.util.RedisDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.moim.backend.domain.space.response.space.SpaceParticipationsResponse.toParticipateEntity;
import static com.moim.backend.domain.space.response.space.SpaceRegionResponse.toLocalEntity;
import static com.moim.backend.global.common.Result.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SpaceService {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final SpaceRepository groupRepository;
    private final ParticipationRepository participationRepository;
    private final BestPlaceRepository bestPlaceRepository;
    private final SubwayRepository subwayRepository;
    private final HotPlaceRepository hotPlaceRepository;
    private final DirectionService directionService;
    private final RedisDao redisDao;
    private final RestTemplate restTemplate = new RestTemplate();

    // 모임 생성
    @Transactional
    public SpaceCreateResponse createSpace(SpaceCreateRequest request, Users user) {
        Space space = groupRepository.save(toGroupEntity(request, user));
        saveParticipation(
                user, space, request.getUserName(),
                request.getLocationName(), request.getLatitude(), request.getLongitude(),
                request.getTransportationType(), request.getPassword()
        );

        saveNearestStationList(space, request.getLatitude(), request.getLongitude());

        return SpaceCreateResponse.response(space);
    }

    private void saveNearestStationList(Space space, Double latitude, Double longitude) {
        List<BestRegion> nearestStationsList =
                subwayRepository.getNearestStationsList(latitude, longitude);

        for (BestRegion bestRegion : nearestStationsList) {
            bestPlaceRepository.save(
                    BestPlace.builder()
                            .space(space)
                            .placeName(bestRegion.getName())
                            .latitude(bestRegion.getLatitude())
                            .longitude(bestRegion.getLongitude())
                            .build()
            );
        }
    }

    // 모임 참여
    @Transactional
    @CacheEvict(value = CacheName.group, key = "#request.getGroupId()")
    public SpaceParticipateResponse participateSpace(SpaceParticipateRequest request, Users user) {
        Space space = getGroup(request.getGroupId());

        participateGroupValidate(request, user, space);
        Participation participation = saveParticipation(
                user, space, request.getUserName(),
                request.getLocationName(), request.getLatitude(), request.getLongitude(),
                request.getTransportationType(), request.getPassword()
        );

        updateBestRegion(space);

        return SpaceParticipateResponse.response(participation);
    }

    private Participation saveParticipation(
            Users user, Space space, String userName,
            String locationName, Double latitude, Double longitude,
            TransportationType transportation, String password
    ) {
        return participationRepository.save(Participation.builder()
                .space(space)
                .userId(user.getUserId())
                .userName(userName)
                .locationName(locationName)
                .latitude(latitude)
                .longitude(longitude)
                .transportation(transportation)
                .password(encrypt(password))
                .build());
    }

    private void participateGroupValidate(SpaceParticipateRequest request, Users user, Space space) {
        validateLocationName(request.getLocationName());
        checkDuplicateParticipation(space, user);
        validateTransportation(request.getTransportationType());
    }

    // 내 참여 정보 수정
    @Transactional
    public SpaceParticipateUpdateResponse participateUpdate(
            SpaceParticipateUpdateRequest request, Users user
    ) {
        Participation myParticipate = getParticipate(request.getParticipateId());
        validateParticipationMyInfo(user, myParticipate);
        // 추천 지역 업데이트 필요한지 확인(업데이트가 필요한 경우: 위치가 변경되었을 때)
        boolean isRequiredBestRegionUpdate = isUpdateBestRegionRequired(request, myParticipate);
        // 참여 정보 수정
        myParticipate.update(request);
        // 추천 지역 업데이트
        if (isRequiredBestRegionUpdate) {
            updateBestRegion(myParticipate.getSpace());
        }
        return SpaceParticipateUpdateResponse.response(myParticipate);
    }

    // 내 모임 나가기
    @Transactional
    public SpaceExitResponse participateExit(Long participateId, Users user) {
        Participation myParticipate = getParticipate(participateId);
        validateParticipationMyInfo(user, myParticipate);

        Space space = myParticipate.getSpace();
        Optional<Vote> optionalVote = voteRepository.findBySpaceId(space.getSpaceId());

        // 투표 시작시 모임 나가기 불가
        checkIfVoteStartedBeforeLeaving(optionalVote);


        // 모임장이 나가는 경우 스페이스 삭제
        if (deleteGroupIfAdminLeaves(user, space)) {
            return SpaceExitResponse.response(true, "모임이 삭제되었습니다.");
        }

        removeParticipation(myParticipate, space);
        return SpaceExitResponse.response(false, "모임에서 나갔습니다.");
    }

    private boolean deleteGroupIfAdminLeaves(Users user, Space space) {
        if (space.getAdminId().equals(user.getUserId())) {
            groupRepository.delete(space);
            voteRepository.deleteBySpaceId(space.getSpaceId());
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
        Space space = participate.getSpace();
        validateAdminStatus(user.getUserId(), space.getAdminId());
        removeParticipation(participate, space);
        return null;
    }

    // 모임 삭제
    @Transactional
    @CacheEvict(value = CacheName.group, key = "#groupId")
    public Void participateDelete(Long groupId, Users user) {
        Space space = getGroup(groupId);
        validateAdminStatus(user.getUserId(), space.getAdminId());
        groupRepository.delete(space);
        voteRepository.deleteBySpaceId(groupId);
        return null;
    }

    public SpaceParticipationsResponse getParticipationDetail(Long groupId, Users user) {
        Space space = getGroup(groupId);
        Participation participation = participationRepository.findBySpaceAndUserId(space, user.getUserId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PARTICIPATE));

        return SpaceParticipationsResponse.toParticipateEntity(space, participation, user);
    }

    // 모임 추천 지역 조회하기
    @TimeCheck
    @Cacheable(value = CacheName.group, key = "#groupId")
    public List<PlaceRouteResponse> getBestRegion(Long groupId) {
        Space space = getGroup(groupId);
        List<Participation> participationList = participationRepository.findAllBySpace(space);

        return bestPlaceRepository.findAllBySpace(space).stream().map(bestPlace -> new PlaceRouteResponse(
                bestPlace, getMoveUserInfoList(bestPlace, space, participationList)
        )).collect(Collectors.toList());
    }

    // 내 모임 확인하기
    public List<SpaceMyParticipateResponse> getMyParticipate(Users user) {
        List<Space> groups = groupRepository.findBySpaceFetch(user.getUserId());

        return groups.stream()
                .map(group -> toMyParticiPateResponse(group, user))
                .toList();
    }

    private static SpaceMyParticipateResponse toMyParticiPateResponse(Space space, Users user) {
        Participation admin = getGroupAdmin(space);
        return SpaceMyParticipateResponse.response(
                space,
                admin.getUserName(),
                admin.getUserId().equals(user.getUserId()),
                space.getBestPlaces().stream().map(BestPlace::getPlaceName).toList(),
                space.getParticipations().stream().map(Participation::getUserName).toList());
    }

    private static Participation getGroupAdmin(Space space) {
        return space.getParticipations().stream()
                .filter(participation -> participation.getUserId().equals(space.getAdminId()))
                .findFirst()
                .orElseThrow(
                        () -> new CustomException(NOT_FOUND_PARTICIPATE)
                );
    }

    // 모임 장소 추천 조회 리스트 API
    public List<SpacePlaceResponse> keywordCentralizedMeetingSpot(Double x, Double y, String local, String keyword) {
        // 네이버 API 요청
        String station = getStation(local);
        URI uri = createNaverRequestUri(station, keyword);
        ResponseEntity<NaverMapListDto> naverResponse = restTemplate.getForEntity(uri.toString(), NaverMapListDto.class);

        // 응답 처리
        Optional<NaverMapListDto> optionalBody = Optional.ofNullable(naverResponse.getBody());
        if (!naverResponse.getStatusCode().is2xxSuccessful() || optionalBody.isEmpty()) {
            throw new CustomException(NOT_REQUEST_NAVER);
        }

        // 장소 정보 가져오기
        List<NaverMapListDto.placeList> placeList = optionalBody.get().getResult().getPlace().getList();

        return placeList.stream().map(toPlaceEntity(x, y, station)).toList();
    }

    private static String getStation(String local) {
        String station = local;
        int index = local.indexOf("("); // 괄호의 위치를 찾음
        if (index != -1) { // 괄호가 존재한다면
            station = local.substring(0, index);
        }

        return station;
    }

    // 모임 참여자 정보 리스트 조회 API
    public SpaceDetailResponse readParticipateSpaceByRegion(Long groupId) {
        Space space = getGroupByFetchParticipation(groupId);
        Users admin = getUser(space.getAdminId());
        List<SpaceRegionResponse> regions = new ArrayList<>();

        space.getParticipations().forEach(participation -> toRegionsResponse(space, regions, participation));

        return SpaceDetailResponse.response(space, admin, regions);
    }

    public NicknameValidationResponse checkNicknameValidation(Long groupId, String nickname) {
        Space space = getGroup(groupId);
        List<Participation> participationList = participationRepository.findAllBySpaceAndUserName(space, nickname);
        boolean isDuplicated = (participationList.size() > 0) ? true : false;
        return new NicknameValidationResponse(isDuplicated);
    }

    private void toRegionsResponse(Space space, List<SpaceRegionResponse> regions, Participation participation) {
        // 그룹화 지역 이름 생성
        String regionName = getRegionName(participation);

        // 참여 정보 응답 객체 변환
        Users user = getUser(participation.getUserId());
        SpaceParticipationsResponse participateEntity = toParticipateEntity(space, participation, user);

        // 생성된 그룹화 지역 이름과 일치하는 그룹화 지역이 이미 존재하는지 Optional 검증
        Optional<SpaceRegionResponse> optionalRegion = findRegionByName(regions, regionName);

        // 검증에 맞추어 Region 업데이트
        toUpdateRegion(regions, regionName, participateEntity, optionalRegion);
    }

    private static void toUpdateRegion(List<SpaceRegionResponse> regions, String regionName, SpaceParticipationsResponse participateEntity, Optional<SpaceRegionResponse> optionalRegion) {
        if (optionalRegion.isEmpty()) { // 존재하지 않는다면
            addNewRegion(regions, regionName, participateEntity);
        } else { // 존재한다면
            addParticipationToExistingRegion(participateEntity, optionalRegion.get());
        }
    }

    private static void addParticipationToExistingRegion(SpaceParticipationsResponse participateEntity, SpaceRegionResponse region) {
        List<SpaceParticipationsResponse> participations = new ArrayList<>(region.getParticipations());
        participations.add(participateEntity);
        region.setParticipations(participations);
    }

    private static void addNewRegion(List<SpaceRegionResponse> regions, String regionName, SpaceParticipationsResponse participateEntity) {
        regions.add(toLocalEntity(regionName, participateEntity));
    }

    private static Optional<SpaceRegionResponse> findRegionByName(List<SpaceRegionResponse> regions, String regionName) {
        return regions.stream()
                .filter(local -> local.getRegionName().equals(regionName))
                .findFirst();
    }

    // 모임 이름 수정 API
    @Transactional
    public Void updateSpaceName(Long groupId, SpaceNameUpdateRequest request, Users user) {
        Space space = getGroup(groupId);
        validateAdminStatus(user.getUserId(), space.getAdminId());
        space.updateGroupName(request.getGroupName());
        return null;
    }

    // 모임 전체 나가기 API
    public Void allParticipateExit(Users user) {
        Long userId = user.getUserId();
        List<Participation> participations = participationRepository.findByUserId(userId);

        for (Participation participation : participations) {
            Long groupId = participation.getSpace().getSpaceId();
            Optional<Vote> optionalVote = voteRepository.findBySpaceId(groupId);
            if (optionalVote.isPresent() && !optionalVote.get().getIsClosed()) {
                throw new CustomException(NOT_ALL_EXIT_PARTICIPATE);
            }
        }

        participationRepository.deleteByUserId(userId);
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

    private void checkDuplicateParticipation(Space space, Users user) {
        if (participationRepository.countBySpaceAndUserId(space, user.getUserId()) > 0) {
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
        String[] locationName = participation.getLocationName().split(" ");
        if (locationName.length < 2) {
            return participation.getLocationName();
        }
        return String.format("%s %s", locationName[0], locationName[1]);
    }

    private static Function<NaverMapListDto.placeList, SpacePlaceResponse> toPlaceEntity(Double x, Double y, String local) {
        return naver -> {
            SpacePlaceResponse response = SpacePlaceResponse.response(naver, local);
            double placeX = Double.parseDouble(naver.getX());
            double placeY = Double.parseDouble(naver.getY());
            String distance = String.format("%s(으)로부터 %sm", local, (int) DistanceCalculator.getDistance(y, x, placeY, placeX));
            response.setDistance(distance);
            return response;
        };
    }

    private Space getGroupByFetchParticipation(Long groupId) {
        return groupRepository.findBySpaceParticipation(groupId).orElseThrow(
                () -> new CustomException(NOT_FOUND_GROUP)
        );
    }

    private Space getGroup(Long groupId) {
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

    private Space toGroupEntity(SpaceCreateRequest request, Users user) {
        return Space.builder()
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
            Space space,
            List<Participation> participationList
    ) {
        List<PlaceRouteResponse.MoveUserInfo> moveUserInfoList = new ArrayList<>();

        participationList.forEach(participation -> {
            if ((participation.getTransportation() == TransportationType.PUBLIC)) {
                directionService.getBusRouteToResponse(bestPlace, space, participation)
                        .ifPresent(moveUserInfo -> moveUserInfoList.add(moveUserInfo));
            } else if (participation.getTransportation() == TransportationType.PERSONAL) {
                directionService.getCarRouteToResponse(bestPlace, space, participation)
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

    private void removeParticipation(Participation participate, Space space) {
        participationRepository.delete(participate);
        // 추천 지역 다시 계산
        updateBestRegion(space);
    }

    private void updateBestRegion(Space space) {
        // best-place 테이블에서 관련 정보 삭제
        List<BestPlace> bestPlaces = bestPlaceRepository.findAllBySpace(space);
        for (BestPlace bestPlace : bestPlaces) {
            bestPlaceRepository.delete(bestPlace);
        }

        // 관련 Redis 삭제
        redisDao.deleteSpringCache(CacheName.group, space.getSpaceId().toString());

        // 추천 지역 다시 계산 후 best-place 테이블 업데이트
        for (BestRegion bestRegion : calculateBestPlaces(space)) {
            bestPlaceRepository.save(
                    BestPlace.builder()
                            .space(space)
                            .placeName(bestRegion.getName())
                            .latitude(bestRegion.getLatitude())
                            .longitude(bestRegion.getLongitude())
                            .build()
            );
        }
    }

    private List<BestRegion> calculateBestPlaces(Space space) {
        // 중간 좌표 구하기
        MiddlePoint middlePoint = participationRepository.getMiddlePoint(space);

        // 유효범위 찾기
        List<Participation> participationList = participationRepository.findAllBySpace(space);
        double validRange = getValidRange(participationList, middlePoint);

        // 근처 지하철역 찾기
        List<BestRegion> bestRegionList = subwayRepository.getNearestStationsList(
                middlePoint.getLatitude(), middlePoint.getLongitude(), validRange
        );

        // 근처에 지하철역이 없다면, 인기 장소 찾기
        if (bestRegionList.isEmpty()) {
            bestRegionList = hotPlaceRepository.getNearestHotPlaceList(
                    middlePoint.getLatitude(), middlePoint.getLongitude(), validRange
            );
        }

        return bestRegionList;
    }

    // 추천 지역 업데이트가 필요한지에 대한 여부 판단. 참여자의 위치 정보가 수정되었다면 업데이트 필요.
    private boolean isUpdateBestRegionRequired(SpaceParticipateUpdateRequest request, Participation myParticipate) {
        if (request.getLatitude() != myParticipate.getLatitude() || request.getLongitude() != myParticipate.getLongitude()) {
            return true;
        }
        return false;
    }
}
