package com.moim.backend.domain.space.service;

import com.moim.backend.domain.groupvote.entity.SelectPlace;
import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.groupvote.repository.SelectPlaceRepository;
import com.moim.backend.domain.groupvote.repository.VoteRepository;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.repository.BestPlaceRepository;
import com.moim.backend.domain.space.repository.GroupRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.space.request.controller.GroupCreateRequest;
import com.moim.backend.domain.space.request.controller.GroupNameUpdateRequest;
import com.moim.backend.domain.space.request.controller.GroupParticipateRequest;
import com.moim.backend.domain.space.request.controller.GroupParticipateUpdateRequest;
import com.moim.backend.domain.space.request.service.GroupParticipateServiceRequest;
import com.moim.backend.domain.space.response.group.*;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.global.common.Result;
import com.moim.backend.global.common.exception.CustomException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.moim.backend.domain.space.entity.TransportationType.PERSONAL;
import static com.moim.backend.domain.space.entity.TransportationType.PUBLIC;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GroupServiceTest {
    @Autowired
    private GroupService groupService;

    @Autowired
    private EntityManager em;

    @Autowired
    private BestPlaceRepository bestPlaceRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SelectPlaceRepository selectPlaceRepository;

    @Autowired
    private VoteRepository voteRepository;

    @DisplayName("하나의 유저가 새로운 모임을 생성한다.")
    @Test
    void create() {
        // given
        Users user = savedUser("test@test.com", "테스트 이름");

        GroupCreateRequest request = GroupCreateRequest.toRequest(
                "테스트 그룹", LocalDate.of(2023, 7, 15), "천이닷",
                "서울 성북구 보문로34다길 2", 37.591043, 127.019721,
                PUBLIC, null
        );

        // when
        GroupCreateResponse response = groupService.createGroup(request.toServiceRequest(), user);
        em.flush();
        em.clear();

        // then
        assertThat(response)
                .extracting("adminId", "name", "date", "fixedPlace")
                .contains(user.getUserId(), "테스트 그룹", "2023-07-15", null);

        Groups group = groupRepository.findById(response.getGroupId()).get();
        assertThat(group.getBestPlaces())
                .hasSize(3)
                .extracting("placeName")
                .contains("성신여대입구(돈암)", "보문", "안암(고대병원앞)");
    }

    @DisplayName("하나의 유저가 새로운 모임을 생성할때 날짜는 입력하지 않는다.")
    @Test
    void createNotEnteredDate() {
        // given
        Users user = savedUser("test@test.com", "테스트 이름");

        GroupCreateRequest request = GroupCreateRequest.toRequest(
                "테스트 그룹", null, "천이닷",
                "서울 성북구 보문로34다길 2", 37.591043, 127.019721,
                PUBLIC, null
        );

        // when
        GroupCreateResponse response = groupService.createGroup(request.toServiceRequest(), user);

        // then
        assertThat(response.getGroupId()).isNotNull();
        assertThat(response)
                .extracting("adminId", "name", "date", "fixedPlace")
                .contains(user.getUserId(), "테스트 그룹", "none", "none");
    }

    @DisplayName("하나의 유저가 하나의 그룹에 참가한다.")
    @Test
    void participate() {
        // given
        Users user = savedUser("test@test.com", "테스트 이름");

        Users participateUser = savedUser("test2@test.com", "테스트 이름2");

        Groups saveGroup = savedGroup(user.getUserId(), "테스트 그룹");

        GroupParticipateRequest request = GroupParticipateRequest.toRequest(
                saveGroup.getGroupId(), "경기도불주먹", "서울 성북구 보문로34다길 2", 37.5660, 126.9784,
                PUBLIC, "2345"
        );

        // when
        GroupParticipateResponse response =
                groupService.participateGroup(request.toServiceRequest(), participateUser);

        // then
        assertThat(response.getParticipationId()).isNotNull();
        assertThat(response)
                .extracting(
                        "groupId", "userId", "locationName",
                        "userName", "latitude", "longitude",
                        "transportation"
                )
                .contains(
                        saveGroup.getGroupId(), participateUser.getUserId(), "서울 성북구 보문로34다길 2",
                        "경기도불주먹", 37.5660, 126.9784, "PUBLIC"
                );

        Participation participation = participationRepository.findById(response.getParticipationId()).get();
        assertThat(participation.getPassword()).isEqualTo(encrypt("2345"));
    }

    @DisplayName("하나의 유저가 하나의 그룹에 참가할때 위치 이름이 잘못됐을 경우 Exception 이 발생한다.")
    @Test
    void participateWithValidateLocationThrowException() {
        // given
        Users user = savedUser("test@test.com", "테스트 이름");
        Users participateUser = savedUser("test2@test.com", "테스트 이름2");
        Groups saveGroup = savedGroup(user.getUserId(), "테스트 그룹");

        GroupParticipateRequest request = GroupParticipateRequest.toRequest(
                saveGroup.getGroupId(), "경기도불주먹", "서울", 37.5660, 126.9784,
                PUBLIC, "2345"
        );

        // when //then
        assertThatThrownBy(() -> groupService.participateGroup(request.toServiceRequest(), participateUser))
                .extracting("result.code", "result.message")
                .contains(-1009, "잘못된 지역 이름 입니다.");
    }

    @DisplayName("하나의 유저가 하나의 그룹에 참가할때 비밀번호를 입력하지 않는다.")
    @Test
    void participateNotEnteredPassword() {
        // given
        Users user = savedUser("test@test.com", "테스트 이름");

        Users participateUser = savedUser("test2@test.com", "테스트 이름2");

        Groups saveGroup = savedGroup(user.getUserId(), "테스트 그룹");

        GroupParticipateRequest request =
                GroupParticipateRequest.toRequest(
                        saveGroup.getGroupId(), "경기도불주먹", "서울 성북구 보문로34다길 2",
                        37.5660, 126.9784, PUBLIC, null
                );

        // when
        GroupParticipateResponse response =
                groupService.participateGroup(request.toServiceRequest(), participateUser);

        // then
        assertThat(response)
                .extracting(
                        "groupId", "locationName",
                        "userId", "userName", "latitude",
                        "longitude", "transportation"
                )
                .contains(
                        saveGroup.getGroupId(), "서울 성북구 보문로34다길 2",
                        participateUser.getUserId(), "경기도불주먹", 37.5660,
                        126.9784, "PUBLIC"
                );

        Participation participation = participationRepository.findById(response.getParticipationId()).get();
        assertThat(participation.getPassword()).isNull();
    }

    @DisplayName("하나의 유저가 하나의 그룹에 참가할때 이동수단이 PUBLIC, PERSONAL 이 아닌 잘못된 코드를 보내면 Exception 이 발생한다.")
    @Test
    void throwsExceptionWhenParticipateWithInvalidTransportation() {
        // given
        Groups saveGroup = savedGroup(1L, "모임장");

        Users user = savedUser("test@test.com", "테스트 이름");

        GroupParticipateRequest request = GroupParticipateRequest.toRequest(
                saveGroup.getGroupId(), "경기도불주먹", "서울 성북구 보문로34다길 2",
                37.5660, 126.9784, TransportationType.NULL, "2345"
        );

        // when // then
        assertThatThrownBy(() -> groupService.participateGroup(request.toServiceRequest(), user))
                .extracting("result.code", "result.message")
                .contains(-1002, "잘못된 이동수단 입니다.");
    }

    @DisplayName("자신이 속해있는 그룹에서 나의 참여정보를 수정한다.")
    @Test
    void participateUpdate() {
        // given
        Users admin = savedUser("test@test.com", "테스트 이름");

        Users user1 = savedUser("test2@test.com", "테스트 이름2");

        Groups group = savedGroup(admin.getUserId(), "모임장");

        Participation user1Participation = savedParticipation(
                user1, group, user1.getName(),
                "커피나무", 37.5660, 126.9784, PUBLIC
        );

        GroupParticipateUpdateRequest request = GroupParticipateUpdateRequest.toRequest(
                user1Participation.getParticipationId(), "양파쿵야", "뮬", 37.5700, 126.9790, PERSONAL
        );

        // when
        GroupParticipateUpdateResponse response =
                groupService.participateUpdate(request.toServiceRequest(), user1);

        // then
        assertThat(response)
                .extracting("locationName", "transportation")
                .contains("뮬", "PERSONAL");
    }

    @DisplayName("자신이 속해있는 그룹에서 내가 아닌 참여정보를 수정하려할때 Exception 이 발생한다.")
    @Test
    void throwsExceptionWhenModifyingUnauthorizedParticipantInfo() {
        // given
        Users admin = savedUser("test@test.com", "테스트 이름");

        Users user1 = savedUser("test2@test.com", "테스트 이름2");

        Groups group = savedGroup(admin.getUserId(), "모임장");

        Participation user1Participation = savedParticipation(
                user1, group, user1.getName(),
                "커피나무", 37.5660, 126.9784, PUBLIC
        );

        Participation adminParticipation = savedParticipation(
                admin, group, admin.getName(),
                "커피나무", 37.5660, 126.9784, PUBLIC
        );

        GroupParticipateUpdateRequest request = GroupParticipateUpdateRequest.toRequest(
                user1Participation.getParticipationId(), "양파쿵야", "뮬", 37.5700, 126.9790, PERSONAL
        );

        // when // then
        assertThatThrownBy(() -> groupService.participateUpdate(request.toServiceRequest(), admin))
                .extracting("result.code", "result.message")
                .contains(-1004, "자신의 참여 정보가 아닙니다.");
    }

    @DisplayName("모임원이 속해있는 모임에서 나가기를 한다.")
    @Test
    void participateExit() {
        // given
        Users admin = savedUser("admin@test.com", "테스트 어드민");
        Users user = savedUser("test@test.com", "테스트 이름");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");
        Participation participationAdmin =
                savedParticipation(admin, group, "어드민", "어딘가", 37.5660, 126.1234, PUBLIC);
        Participation participationUser =
                savedParticipation(user, group, "참여자", "어딘가", 37.5660, 126.1234, PUBLIC);

        // when
        GroupExitResponse response =
                groupService.participateExit(participationUser.getParticipationId(), user);

        // then
        assertThat(response)
                .extracting("isDeletedSpace", "message")
                .contains(false, "모임에서 나갔습니다.");

        Optional<Participation> optionalParticipation =
                participationRepository.findById(participationUser.getParticipationId());

        assertThat(optionalParticipation.isEmpty()).isTrue();
    }

    @DisplayName("모임장이 모임에서 나가기를 하면 해당 모임이 삭제된다.")
    @Test
    void groupAdminExitsThenGroupIsDeleted() {

        // given
        Users admin = savedUser("admin@test.com", "테스트 어드민");
        Users user = savedUser("test@test.com", "테스트 이름");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");
        Participation participationAdmin =
                savedParticipation(admin, group, "어드민", "어딘가", 37.5660, 126.1234, PUBLIC);
        Participation participationUser =
                savedParticipation(user, group, "참여자", "어딘가", 37.5660, 126.1234, PUBLIC);

        em.flush();
        em.clear();

        // when
        GroupExitResponse response =
                groupService.participateExit(participationAdmin.getParticipationId(), admin);

        // then
        assertThat(response)
                .extracting("isDeletedSpace", "message")
                .contains(true, "모임이 삭제되었습니다.");

        Optional<Groups> optionalGroup = groupRepository.findById(group.getGroupId());

        Optional<Participation> optionalParticipation =
                participationRepository.findById(participationUser.getParticipationId());

        assertThat(optionalGroup.isEmpty()).isTrue();
        assertThat(optionalParticipation.isEmpty()).isTrue();
    }

    @DisplayName("모임장이 모임원을 내보낸다.")
    @Test
    void participateRemoval() {
        // given
        Users admin = savedUser("admin@test.com", "테스트 어드민");
        Users user = savedUser("test@test.com", "테스트 이름");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");
        Participation participationAdmin =
                savedParticipation(admin, group, "어드민", "어딘가", 37.5660, 126.1234, PUBLIC);
        Participation participationUser =
                savedParticipation(user, group, "참여자", "어딘가", 37.5660, 126.1234, PUBLIC);

        // when
        groupService.participateRemoval(participationUser.getParticipationId(), admin);

        // then
        Optional<Participation> optionalParticipation =
                participationRepository.findById(participationUser.getParticipationId());

        assertThat(optionalParticipation.isEmpty()).isTrue();
    }

    @DisplayName("모임원이 모임원을 내보내려하면 어드민이 아니기 때문에 Exception 이 발생한다.")
    @Test
    void participateRemovalNotAdmin() {
        // given
        Users admin = savedUser("admin@test.com", "테스트 어드민");
        Users user = savedUser("test@test.com", "테스트 이름");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");
        Participation participationAdmin =
                savedParticipation(admin, group, "어드민", "어딘가", 37.5660, 126.1234, PUBLIC);
        Participation participationUser =
                savedParticipation(user, group, "참여자", "어딘가", 37.5660, 126.1234, PUBLIC);

        // when // then
        assertThatThrownBy(() -> groupService.participateRemoval(participationAdmin.getParticipationId(), user))
                .extracting("result.code", "result.message")
                .contains(-1005, "해당 유저는 그룹의 어드민이 아닙니다.");
    }

    @DisplayName("모임장이 모임을 삭제한다")
    @Test
    void groupDelete() {
        // given
        Users admin = savedUser("admin@test.com", "테스트 어드민");
        Users user = savedUser("test@test.com", "테스트 이름");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");

        Participation participationAdmin =
                savedParticipation(admin, group, "어드민", "어딘가", 37.5660, 126.1234, PUBLIC);
        Participation participationUser =
                savedParticipation(user, group, "참여자", "어딘가", 37.5660, 126.1234, PUBLIC);

        BestPlace bestPlace1 = saveBestPlace(group, "성신여대입구역", 1.0, 1.0);

        Vote vote = voteRepository.save(
                Vote.builder()
                        .groupId(group.getGroupId())
                        .isClosed(false)
                        .isAnonymous(false)
                        .isEnabledMultipleChoice(true)
                        .endAt(LocalDateTime.of(2023, 9, 23, 15, 0))
                        .build()
        );

        SelectPlace selectPlace1 = saveSelectPlace(admin.getUserId(), bestPlace1, vote);

        em.flush();
        em.clear();

        // when
        groupService.participateDelete(group.getGroupId(), admin);

        // then
        Optional<Groups> optionalGroup = groupRepository.findById(group.getGroupId());
        Optional<Participation> optionalParticipation =
                participationRepository.findById(participationUser.getParticipationId());
        Optional<BestPlace> optionalBestPlace =
                bestPlaceRepository.findById(bestPlace1.getBestPlaceId());
        Optional<SelectPlace> optionalSelectPlace =
                selectPlaceRepository.findById(selectPlace1.getSelectPlaceId());
        Optional<Vote> optionalVote =
                voteRepository.findById(vote.getVoteId());

        assertThat(optionalGroup.isEmpty()).isTrue();
        assertThat(optionalParticipation.isEmpty()).isTrue();
        assertThat(optionalBestPlace.isEmpty()).isTrue();
        assertThat(optionalSelectPlace.isEmpty()).isTrue();
        assertThat(optionalVote.isEmpty()).isTrue();
    }

    private SelectPlace saveSelectPlace(Long userId, BestPlace bestPlace, Vote vote) {
        return selectPlaceRepository.save(
                SelectPlace.builder()
                        .vote(vote)
                        .bestPlace(bestPlace)
                        .userId(userId)
                        .build()
        );
    }

    @DisplayName("모임장이 모임을 삭제한다")
    @Test
    void groupDeleteThrowThatNotAdmin() {
        // given
        Users admin = savedUser("admin@test.com", "테스트 어드민");
        Users user = savedUser("test@test.com", "테스트 이름");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");

        // when // then
        assertThatThrownBy(() -> groupService.participateDelete(group.getGroupId(), user))
                .extracting("result.code", "result.message")
                .contains(-1005, "해당 유저는 그룹의 어드민이 아닙니다.");

    }

    @DisplayName("유저가 자신의 모임들을 확인한다.")
    @Test
    void getMyParticipate() {
        // given
        Users admin1 = savedUser("admin1@test.com", "어드민1");
        Users admin2 = savedUser("admin2@test.com", "어드민2");
        Users admin3 = savedUser("admin3@test.com", "어드민3");

        Groups group1 = savedGroup(admin1.getUserId(), "그룹1");
        Groups group2 = savedGroup(admin2.getUserId(), "그룹2");
        Groups group3 = savedGroup(admin3.getUserId(), "그룹3");

        saveBestPlace(group1, "의정부역", 127.123456, 36.123456);
        saveBestPlace(group1, "서울역", 127.123457, 36.123457);
        saveBestPlace(group1, "개봉역", 127.123458, 36.123458);

        Users user1 = savedUser("test1@test.com", "테스트1");
        Users user2 = savedUser("test2@test.com", "테스트2");

        savedParticipation(admin1, group1, "어드민", "아무데나", 36.23423, 127.32423, PUBLIC);
        savedParticipation(admin2, group2, "어드민", "아무데나", 36.23423, 127.32423, PUBLIC);
        savedParticipation(admin3, group3, "어드민", "아무데나", 36.23423, 127.32423, PUBLIC);

        savedParticipation(user1, group1, "양쿵", "아무데나", 36.23423, 127.32423, PUBLIC);
        savedParticipation(user1, group2, "양쿵", "아무데나", 36.23423, 127.32423, PUBLIC);
        savedParticipation(user1, group3, "양쿵", "아무데나", 36.23423, 127.32423, PUBLIC);

        savedParticipation(user2, group1, "주쿵", "아무데나", 36.23423, 127.32423, PUBLIC);
        savedParticipation(user2, group3, "주쿵", "아무데나", 36.23423, 127.32423, PUBLIC);

        em.flush();
        em.clear();

        // when
        List<GroupMyParticipateResponse> response = groupService.getMyParticipate(user1);

        // then
        assertThat(response).hasSize(3);

        assertThat(response.get(0))
                .extracting("groupId", "groupName", "groupAdminName", "groupDate", "groupParticipates", "confirmPlace")
                .contains(group1.getGroupId(), "어드민", "그룹1", "2023-07-10", 3, "none");

        assertThat(response.get(0).getParticipantNames()).isEqualTo(List.of("어드민", "양쿵", "주쿵"));

        assertThat(response.get(0).getBestPlaceNames())
                .hasSize(3)
                .contains("의정부역", "서울역", "개봉역");
    }

    @DisplayName("유저가 자신의 모임들을 확인할때 아직 아무것도 참여하지 않았다.")
    @Test
    void getMyParticipateIsNonList() {
        // given
        Users admin1 = savedUser("admin1@test.com", "어드민1");

        em.flush();
        em.clear();

        // when
        List<GroupMyParticipateResponse> response = groupService.getMyParticipate(admin1);

        // then
        assertThat(response).isEmpty();
    }

    @DisplayName("동일한 유저가 동일한 그룹에 중복으로 참여할 수 없다.")
    @Test
    void throwsExceptionWhenDuplicateParticipation() {
        //given
        Users user = savedUser("test@gmail.com", "테스터");
        Groups group = savedGroup(user.getUserId(), "테스트 그룹");
        GroupParticipateServiceRequest request = GroupParticipateServiceRequest.builder()
                .groupId(group.getGroupId())
                .userName("테스터")
                .locationName("서울 성북구 보문로34다길 2")
                .latitude(37.610553)
                .longitude(126.92982)
                .transportationType(PUBLIC)
                .build();

        groupService.participateGroup(request, user);

        // when // then
        assertThatThrownBy(() -> groupService.participateGroup(request, user))
                .extracting("result.code", "result.message")
                .contains(Result.DUPLICATE_PARTICIPATION.getCode(), Result.DUPLICATE_PARTICIPATION.getMessage());
    }

    @DisplayName("네이버 API 를 이용해 장소의 정보를 가져온다.")
    @Test
    void keywordCentralizedMeetingSpot() throws UnsupportedEncodingException {
        // given

        // when
        List<GroupPlaceResponse> response = groupService.keywordCentralizedMeetingSpot(
                127.01674669413555, 37.59276455965626, "성신여대입구역", "카페"
        );

        // then
        assertThat(response)
                .hasSize(12)
                .allSatisfy(place -> {
                    assertThat(place.getTitle()).isNotNull();
                    assertThat(place.getDetail().getX()).isNotNull();
                    assertThat(place.getDetail().getY()).isNotNull();
                    assertThat(place.getDetail().getAddress()).isNotNull();
                    assertThat(place.getDistance()).isNotNull();
                });
    }

    @DisplayName("유저가 해당 모임의 참여자 정보들을 조회한다.")
    @Test
    void readParticipateGroupByRegion() {
        // given
        Users user1 = savedUser("test1@test.com", "모이닷 운영자1");
        Users user2 = savedUser("test2@test.com", "모이닷 운영자2");
        Users user3 = savedUser("test3@test.com", "모이닷 운영자3");
        Users user4 = savedUser("test4@test.com", "모이닷 운영자4");

        Groups group = savedGroup(user1.getUserId(), "모이닷");

        Participation participation1 = savedParticipation(user1, group, "모이닷1", "서울 성북구 보문로34다길 2", 36.123456, 127.1234567, PERSONAL);
        Participation participation2 = savedParticipation(user2, group, "모이닷2", "서울 강북구 도봉로 76가길 55", 36.123456, 127.1234567, PUBLIC);
        Participation participation3 = savedParticipation(user3, group, "모이닷3", "서울 강북구 도봉로 76가길 54", 36.123456, 127.1234567, PERSONAL);
        Participation participation4 = savedParticipation(user4, group, "모이닷4", "경기도 부천시 부천로 1", 36.123456, 127.1234567, PUBLIC);

        em.flush();
        em.clear();

        // when
        GroupDetailResponse response = groupService.readParticipateGroupByRegion(group.getGroupId());

        // then
        assertThat(response)
                .extracting("groupId", "name", "adminEmail", "date")
                .contains(group.getGroupId(), "모이닷", user1.getEmail(), "2023-07-10");

        assertThat(response.getParticipantsByRegion())
                .extracting("regionName")
                .contains("서울 성북구", "서울 강북구", "경기도 부천시");

        assertThat(response.getParticipantsByRegion())
                .allSatisfy(region -> {
                    if (region.getRegionName().equals("서울 성북구")) {
                        assertThat(region.getParticipations())
                                .extracting("userName", "locationName")
                                .contains(tuple("모이닷1", "서울 성북구 보문로34다길 2"));
                    } else if (region.getRegionName().equals("서울 강북구")) {
                        assertThat(region.getParticipations())
                                .extracting("userName", "locationName")
                                .contains(
                                        tuple("모이닷2", "서울 강북구 도봉로 76가길 55"),
                                        tuple("모이닷3", "서울 강북구 도봉로 76가길 54")
                                );
                    } else {
                        assertThat(region.getParticipations())
                                .extracting("userName", "locationName")
                                .contains(tuple("모이닷4", "경기도 부천시 부천로 1"));
                    }
                });
    }

    @DisplayName("모임장이 스페이스 이름을 수정한다.")
    @Test
    void updateGroupName() {
        // given
        Users user = savedUser("test@test.com", "테스트 이름");
        Groups saveGroup = savedGroup(user.getUserId(), "테스트 그룹");
        GroupNameUpdateRequest request = new GroupNameUpdateRequest("그룹이름변경");

        // when
        groupService.updateGroupName(saveGroup.getGroupId(), request.toServiceRequest(), user);

        // then
        em.flush();
        em.clear();
        assertThat(saveGroup.getName()).isEqualTo("그룹이름변경");
    }

    // method
    private BestPlace saveBestPlace(Groups group, String placeName, double longitude, double latitude) {
        return bestPlaceRepository.save(
                BestPlace.builder()
                        .group(group)
                        .placeName(placeName)
                        .longitude(longitude)
                        .latitude(latitude)
                        .build()
        );
    }

    private Groups savedGroup(Long userId, String name) {
        return groupRepository.save(
                Groups.builder()
                        .adminId(userId)
                        .name(name)
                        .place("none")
                        .date(LocalDate.of(2023, 7, 10))
                        .build()
        );
    }

    private Participation savedParticipation(
            Users user, Groups group, String userName,
            String locationName, Double latitude, Double longitude,
            TransportationType transportationType
    ) {
        return participationRepository.save(
                Participation.builder()
                        .group(group)
                        .userId(user.getUserId())
                        .userName(userName)
                        .locationName(locationName)
                        .latitude(latitude)
                        .longitude(longitude)
                        .transportation(transportationType)
                        .build()
        );
    }

    private Users savedUser(String email, String name) {
        return userRepository.save(
                Users.builder()
                        .email(email)
                        .name(name)
                        .build()
        );
    }

    public static String encrypt(String password) {
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
            throw new CustomException(Result.FAIL);
        }
    }
}