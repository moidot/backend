package com.moim.backend.domain.space.service;

import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.groupvote.repository.VoteRepository;
import com.moim.backend.domain.space.config.OdsayProperties;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.repository.BestPlaceRepository;
import com.moim.backend.domain.space.repository.GroupRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.space.request.GroupServiceRequest;
import com.moim.backend.domain.space.response.GroupResponse;
import com.moim.backend.domain.space.response.KakaoMapDetailDto;
import com.moim.backend.domain.space.response.MiddlePoint;
import com.moim.backend.domain.space.response.PlaceRouteResponse;
import com.moim.backend.domain.subway.entity.Subway;
import com.moim.backend.domain.subway.repository.SubwayRepository;
import com.moim.backend.domain.subway.response.BestSubwayInterface;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.space.response.GraphicDataResponse;
import com.moim.backend.domain.space.response.SearchPathResponse;
import com.moim.backend.global.common.Result;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.moim.backend.global.common.Result.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {

    private final VoteRepository voteRepository;
    private final GroupRepository groupRepository;
    private final ParticipationRepository participationRepository;
    private final BestPlaceRepository bestPlaceRepository;
    private final SubwayRepository subwayRepository;
    private final OdsayProperties odsayProperties;
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

        checkDuplicateParticipation(group, user);
        validateTransportation(request.getTransportation());

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

//     불필요한 코드 제거 버전
    public List<PlaceRouteResponse> getBestRegion(Long groupId) {
        Groups group = getGroup(groupId);
        MiddlePoint middlePoint = participationRepository.getMiddlePoint(group);
        List<BestSubwayInterface> bestSubwayList = subwayRepository.getBestSubwayList(
                middlePoint.getLatitude(), middlePoint.getLongitude()
        );

        List<PlaceRouteResponse> placeRouteResponseList = new ArrayList<>();
        List<Participation> participations = participationRepository.findAllByGroup(group);
        bestSubwayList.forEach(bestSubway -> {
            PlaceRouteResponse placeRouteResponse = new PlaceRouteResponse(bestSubway);

            for (Participation participation : participations) {
                addBusRouteToResponse(bestSubway, participation, placeRouteResponse);
            }
            placeRouteResponseList.add(placeRouteResponse);
        });

        return placeRouteResponseList;
    }

    // 내 모임 확인하기
    public List<GroupResponse.MyParticipate> getMyParticipate(Users user) {
        List<Groups> groups = groupRepository.myParticipationGroups(user.getUserId());
        return groups.stream()
                .map(group -> GroupResponse.MyParticipate.response(
                        group, groupBestPlaceToList(group))
                ).toList();
    }


    // validate
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

    private static void validateTransportation(String transportation) {
        if (!transportation.equals("BUS") && !transportation.equals("SUBWAY")) {
            throw new CustomException(INVALID_TRANSPORTATION);
        }
    }

    private static void validateParticipationMyInfo(Users user, Participation myParticipate) {
        if (!myParticipate.getUserId().equals(user.getUserId())) {
            throw new CustomException(NOT_MATCHED_PARTICIPATE);
        }
    }

    // method

    private Groups getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(
                        () -> new CustomException(NOT_FOUND_GROUP)
                );
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
                .transportation(TransportationType.valueOf(request.getTransportation()))
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

    public GroupResponse.detailRecommendedPlace detailRecommendedPlace(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        KakaoMapDetailDto kakaoMapDetailDto;

        try {
            kakaoMapDetailDto = restTemplate.getForObject(
                    "https://place.map.kakao.com/main/v/" + id, KakaoMapDetailDto.class
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new CustomException(UNEXPECTED_EXCEPTION);
        }

        if (kakaoMapDetailDto == null) {
            throw new CustomException(UNEXPECTED_EXCEPTION);
        }

        return GroupResponse.detailRecommendedPlace.response(kakaoMapDetailDto);
    }

    private void addBusRouteToResponse(
            BestSubwayInterface bestSubway, Participation participation, PlaceRouteResponse placeRouteResponse
    ) {
        SearchPathResponse searchPathResponse = restTemplate.getForObject(
                odsayProperties.getSearchPathUriWithParams(bestSubway, participation),
                SearchPathResponse.class
        );

        if (searchPathResponse.getResult() != null) {
            GraphicDataResponse graphicDataResponse = restTemplate.getForObject(
                    odsayProperties.getGraphicDataUriWIthParams(searchPathResponse.getPathInfoMapObj()),
                    GraphicDataResponse.class
            );
            if (graphicDataResponse.getResult() != null) {
                PlaceRouteResponse.MoveUserInfo moveUserInfo = new PlaceRouteResponse.MoveUserInfo(
                        participation, graphicDataResponse, searchPathResponse
                );
                placeRouteResponse.addMoveUserInfo(moveUserInfo);
            }
        }
    }

}
