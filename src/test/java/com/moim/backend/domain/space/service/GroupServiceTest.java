package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.repository.GroupRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.request.GroupRequest;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GroupServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private GroupService groupService;

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
                .contains(user.getUserId(), "테스트 그룹", "2023-07-15", "none");
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

    // method

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