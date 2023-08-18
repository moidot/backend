package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.repository.BestPlaceRepository;
import com.moim.backend.domain.space.repository.GroupRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.space.request.GroupRequest;
import com.moim.backend.domain.space.request.GroupServiceRequest;
import com.moim.backend.domain.space.response.GroupResponse;
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
import java.util.List;
import java.util.Optional;

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

    @DisplayName("하나의 유저가 새로운 모임을 생성한다.")
    @Test
    void create() {
        // given
        Users user = savedUser("test@test.com", "테스트 이름");

        GroupRequest.Create request = new GroupRequest.Create(
                "테스트 그룹", LocalDate.of(2023, 7, 15)
        );

        // when
        GroupResponse.Create response = groupService.createGroup(request.toServiceRequest(), user);

        // then
        assertThat(response)
                .extracting("adminId", "name", "date", "fixedPlace")
                .contains(user.getUserId(), "테스트 그룹", "2023-07-15", null);
    }

    @DisplayName("하나의 유저가 새로운 모임을 생성할때 날짜는 입력하지 않는다.")
    @Test
    void createNotEnteredDate() {
        // given
        Users user = savedUser("test@test.com", "테스트 이름");

        GroupRequest.Create request = new GroupRequest.Create("테스트 그룹", null);

        // when
        GroupResponse.Create response = groupService.createGroup(request.toServiceRequest(), user);

        // then
        assertThat(response.getGroupId()).isNotNull();
        assertThat(response)
                .extracting("adminId", "name", "date", "fixedPlace")
                .contains(user.getUserId(), "테스트 그룹", "none", "none");
    }

    @DisplayName("그룹이 생성된 후 어드민이 모임에 참여하게되면, 추천장소는 어드민 위치 중심으로 3개의 역이 추천된다.")
    @Test
    void participateAdmin() {
        // given
        Users admin = savedUser("admin@test.com", "어드민");
        Users participateUser = savedUser("test2@test.com", "테스트 이름2");
        Groups saveGroup = savedGroup(admin.getUserId(), "테스트 그룹");
        GroupRequest.Participate request = new GroupRequest.Participate(
                saveGroup.getGroupId(), "어드민", "커피나무", 37.591043, 127.019721,
                "BUS", "2345"
        );

        em.flush();
        em.clear();
        // when
        GroupResponse.Participate response =
                groupService.participateGroup(request.toServiceRequest(), admin);

        // then
        Participation participation = participationRepository.findById(response.getParticipationId()).get();
        Groups group = groupRepository.findById(saveGroup.getGroupId()).get();

        assertThat(response.getParticipationId()).isNotNull();
        assertThat(response)
                .extracting(
                        "groupId", "userId", "locationName",
                        "userName", "latitude", "longitude",
                        "transportation"
                )
                .contains(
                        saveGroup.getGroupId(), admin.getUserId(), "커피나무",
                        "어드민", 37.591043, 127.019721,
                        "BUS"
                );

        assertThat(participation.getPassword()).isEqualTo(encrypt("2345"));
        assertThat(group.getBestPlaces())
                .hasSize(3)
                .extracting("placeName")
                .contains("성신여대입구(돈암)", "보문", "안암(고대병원앞)");

    }

    @DisplayName("하나의 유저가 하나의 그룹에 참가한다.")
    @Test
    void participate() {
        // given
        Users user = savedUser("test@test.com", "테스트 이름");

        Users participateUser = savedUser("test2@test.com", "테스트 이름2");

        Groups saveGroup = savedGroup(user.getUserId(), "테스트 그룹");

        GroupRequest.Participate request = new GroupRequest.Participate(
                saveGroup.getGroupId(), "경기도불주먹", "커피나무", 37.5660, 126.9784,
                "BUS", "2345"
        );

        // when
        GroupResponse.Participate response =
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
                        saveGroup.getGroupId(), participateUser.getUserId(), "커피나무",
                        "경기도불주먹", 37.5660, 126.9784,
                        "BUS"
                );

        Participation participation = participationRepository.findById(response.getParticipationId()).get();
        assertThat(participation.getPassword()).isEqualTo(encrypt("2345"));
    }

    @DisplayName("하나의 유저가 하나의 그룹에 참가할때 비밀번호를 입력하지 않는다.")
    @Test
    void participateNotEnteredPassword() {
        // given
        Users user = savedUser("test@test.com", "테스트 이름");

        Users participateUser = savedUser("test2@test.com", "테스트 이름2");

        Groups saveGroup = savedGroup(user.getUserId(), "테스트 그룹");

        GroupRequest.Participate request =
                new GroupRequest.Participate(
                        saveGroup.getGroupId(), "경기도불주먹", "커피나무",
                        37.5660, 126.9784, "BUS", null
                );

        // when
        GroupResponse.Participate response =
                groupService.participateGroup(request.toServiceRequest(), participateUser);

        // then
        assertThat(response)
                .extracting(
                        "groupId", "locationName",
                        "userId", "userName", "latitude",
                        "longitude", "transportation"
                )
                .contains(
                        saveGroup.getGroupId(), "커피나무",
                        participateUser.getUserId(), "경기도불주먹", 37.5660,
                        126.9784, "BUS"
                );

        Participation participation = participationRepository.findById(response.getParticipationId()).get();
        assertThat(participation.getPassword()).isNull();
    }

    @DisplayName("하나의 유저가 하나의 그룹에 참가할때 이동수단이 BUS, SUBWAY 가 아닌 잘못된 코드를 보내면 Exception 이 발생한다.")
    @Test
    void throwsExceptionWhenParticipateWithInvalidTransportation() {
        // given
        Groups saveGroup = savedGroup(1L, "모임장");

        Users user = savedUser("test@test.com", "테스트 이름");

        GroupRequest.Participate request =
                new GroupRequest.Participate(
                        saveGroup.getGroupId(), "경기도불주먹", "커피나무",
                        37.5660, 126.9784, "TAXI", "2345"
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
                "커피나무", 37.5660, 126.9784, "BUS"
        );

        GroupRequest.ParticipateUpdate request = new GroupRequest.ParticipateUpdate(
                user1Participation.getParticipationId(), "양파쿵야", "뮬", 37.5700, 126.9790, "SUBWAY"
        );

        // when
        GroupResponse.ParticipateUpdate response =
                groupService.participateUpdate(request.toServiceRequest(), user1);

        // then
        assertThat(response)
                .extracting("locationName", "transportation")
                .contains("뮬", "SUBWAY");
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
                "커피나무", 37.5660, 126.9784, "BUS"
        );

        Participation adminParticipation = savedParticipation(
                admin, group, admin.getName(),
                "커피나무", 37.5660, 126.9784, "BUS"
        );

        GroupRequest.ParticipateUpdate request = new GroupRequest.ParticipateUpdate(
                user1Participation.getParticipationId(), "양파쿵야", "뮬", 37.5700, 126.9790, "SUBWAY"
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
                savedParticipation(admin, group, "어드민", "어딘가", 37.5660, 126.1234, "BUS");
        Participation participationUser =
                savedParticipation(user, group, "참여자", "어딘가", 37.5660, 126.1234, "BUS");

        // when
        GroupResponse.Exit response =
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
                savedParticipation(admin, group, "어드민", "어딘가", 37.5660, 126.1234, "BUS");
        Participation participationUser =
                savedParticipation(user, group, "참여자", "어딘가", 37.5660, 126.1234, "BUS");

        em.flush();
        em.clear();

        // when
        GroupResponse.Exit response =
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
                savedParticipation(admin, group, "어드민", "어딘가", 37.5660, 126.1234, "BUS");
        Participation participationUser =
                savedParticipation(user, group, "참여자", "어딘가", 37.5660, 126.1234, "BUS");

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
                savedParticipation(admin, group, "어드민", "어딘가", 37.5660, 126.1234, "BUS");
        Participation participationUser =
                savedParticipation(user, group, "참여자", "어딘가", 37.5660, 126.1234, "BUS");

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
                savedParticipation(admin, group, "어드민", "어딘가", 37.5660, 126.1234, "BUS");
        Participation participationUser =
                savedParticipation(user, group, "참여자", "어딘가", 37.5660, 126.1234, "BUS");

        em.flush();
        em.clear();

        // when
        groupService.participateDelete(group.getGroupId(), admin);

        // then
        Optional<Groups> optionalGroup = groupRepository.findById(group.getGroupId());
        Optional<Participation> optionalParticipation =
                participationRepository.findById(participationUser.getParticipationId());

        assertThat(optionalGroup.isEmpty()).isTrue();
        assertThat(optionalParticipation.isEmpty()).isTrue();
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

        savedParticipation(admin1, group1, "어드민", "아무데나", 36.23423, 127.32423, "BUS");
        savedParticipation(admin2, group2, "어드민", "아무데나", 36.23423, 127.32423, "BUS");
        savedParticipation(admin3, group3, "어드민", "아무데나", 36.23423, 127.32423, "BUS");

        savedParticipation(user1, group1, "양쿵", "아무데나", 36.23423, 127.32423, "BUS");
        savedParticipation(user1, group2, "양쿵", "아무데나", 36.23423, 127.32423, "BUS");
        savedParticipation(user1, group3, "양쿵", "아무데나", 36.23423, 127.32423, "BUS");

        savedParticipation(user2, group1, "양쿵", "아무데나", 36.23423, 127.32423, "BUS");
        savedParticipation(user2, group3, "양쿵", "아무데나", 36.23423, 127.32423, "BUS");

        em.flush();
        em.clear();

        // when
        List<GroupResponse.MyParticipate> response = groupService.getMyParticipate(user1);

        // then
        assertThat(response).hasSize(3);

        assertThat(response.get(0))
                .extracting("groupId", "groupName", "groupDate", "groupParticipates")
                .contains(group1.getGroupId(), "그룹1", "2023-07-10", 3);

        assertThat(response.get(0).getBestPlaces())
                .hasSize(3)
                .extracting("bestPlaceName")
                .contains("의정부역", "서울역", "개봉역");
    }

    @DisplayName("동일한 유저가 동일한 그룹에 중복으로 참여할 수 없다.")
    @Test
    void throwsExceptionWhenDuplicateParticipation() {
        //given
        Users user = savedUser("test@gmail.com", "테스터");
        Groups group = savedGroup(user.getUserId(), "테스트 그룹");
        GroupServiceRequest.Participate request = GroupServiceRequest.Participate.builder()
                .groupId(group.getGroupId())
                .userName("테스터")
                .locationName("불광역")
                .latitude(37.610553)
                .longitude(126.92982)
                .transportation("BUS")
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
        List<GroupResponse.Place> response = groupService.keywordCentralizedMeetingSpot(
                127.01674669413555, 37.59276455965626, "성신여대입구역", "카페"
        );

        // then
        assertThat(response)
                .extracting("title", "detail.x", "detail.y", "detail.homePageUrl", "distance")
                .contains(
                        tuple("랠리쉬커피", "127.0166246", "37.5944971", "https://www.instagram.com/relishcoffee_", "성신여대입구역(으)로부터 192m"),
                        tuple("도쿄빙수 성신여대점", "127.0181725", "37.5920195", "https://instagram.com/tokyobingsu_sungshin?igshid=113a9wlh7mmb8", "성신여대입구역(으)로부터 150m"),
                        tuple("Los Dias", "127.0188944", "37.5901777", "http://pf.kakao.com/_cDqxixj", "성신여대입구역(으)로부터 344m"),
                        tuple("753 베이글 비스트로 성신여대점", "127.0200490", "37.5945812", "https://www.instagram.com/753_bagel_bistro", "성신여대입구역(으)로부터 354m"),
                        tuple("서울노마드", "127.0119483", "37.5915893", "http://instagram.com/seoulnomad_official", "성신여대입구역(으)로부터 442m"),
                        tuple("써리얼 벗 나이스", "127.0186908", "37.5945515", "https://www.instagram.com/surreal.b.nice", "성신여대입구역(으)로부터 262m"),
                        tuple("맬크", "127.0168510", "37.5950775", "https://www.instagram.com/melc.cake", "성신여대입구역(으)로부터 257m"),
                        tuple("루틴", "127.0201489", "37.5917147", "http://instagram.com/cafe.routine", "성신여대입구역(으)로부터 321m"),
                        tuple("더홈서울", "127.0174049", "37.5888662", "http://instagram.com/the_home_seoul", "성신여대입구역(으)로부터 437m"),
                        tuple("본크레페", "127.0183100", "37.5920357", "http://www.instagram.com/_bon_crepe_/", "성신여대입구역(으)로부터 159m"),
                        tuple("소설원 서가", "127.0099785", "37.5903636", "", "성신여대입구역(으)로부터 653m"),
                        tuple("모블러", "127.0204521", "37.5950653", "https://smartstore.naver.com/moblerpatisserie", "성신여대입구역(으)로부터 414m")
                );
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

        Participation participation1 = savedParticipation(user1, group, "모이닷1", "서울 성북구 보문로34다길 2", 36.123456, 127.1234567, "SUBWAY");
        Participation participation2 = savedParticipation(user2, group, "모이닷2", "서울 강북구 도봉로 76가길 55", 36.123456, 127.1234567, "BUS");
        Participation participation3 = savedParticipation(user3, group, "모이닷3", "서울 강북구 도봉로 76가길 54", 36.123456, 127.1234567, "SUBWAY");
        Participation participation4 = savedParticipation(user4, group, "모이닷4", "경기도 부천시 부천로 1", 36.123456, 127.1234567, "BUS");

        em.flush();
        em.clear();

        // when
        GroupResponse.Detail response = groupService.readParticipateGroupByRegion(group.getGroupId());

        // then
        assertThat(response)
                .extracting("groupId", "name", "adminId", "date")
                .contains(group.getGroupId(), "모이닷", group.getAdminId(), "2023-07-10");

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

    // method

    private void saveBestPlace(Groups group, String placeName, double longitude, double latitude) {
        bestPlaceRepository.save(
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
            String type
    ) {
        return participationRepository.save(
                Participation.builder()
                        .group(group)
                        .userId(user.getUserId())
                        .userName(userName)
                        .locationName(locationName)
                        .latitude(latitude)
                        .longitude(longitude)
                        .transportation(TransportationType.valueOf(type))
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