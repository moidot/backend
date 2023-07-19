package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.Repository.GroupRepository;
import com.moim.backend.domain.space.Repository.ParticipationRepository;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.request.GroupRequest;
import com.moim.backend.domain.space.response.GroupResponse;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.global.common.Result;
import com.moim.backend.global.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GroupServiceTest {

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
        Users user = userRepository.save(
                Users.builder()
                        .email("test@test.com")
                        .name("테스트 이름")
                        .build()
        );

        GroupRequest.Create request = new GroupRequest.Create(
                "테스트 그룹", LocalDate.of(2023, 7, 15)
        );

        // when
        GroupResponse.Create response = groupService.createGroup(request.toServiceRequest(), user);

        // then
        assertThat(response)
                .extracting("groupId", "adminId", "name", "date", "fixedPlace")
                .contains(1L, user.getUserId(), "테스트 그룹", "2023-07-15", "none");
    }

    @DisplayName("하나의 유저가 새로운 모임을 생성할때 날짜는 입력하지 않는다.")
    @Test
    void createNotEnteredDate() {
        // given
        Users user = userRepository.save(
                Users.builder()
                        .email("test@test.com")
                        .name("테스트 이름")
                        .build()
        );

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
        Users user = userRepository.save(
                Users.builder()
                        .email("test@test.com")
                        .name("테스트 이름")
                        .build()
        );

        Users participateUser = userRepository.save(
                Users.builder()
                        .email("test2@test.com")
                        .name("테스트 이름2")
                        .build()
        );

        Groups saveGroup = groupRepository.save(
                Groups.builder()
                        .adminId(user.getUserId())
                        .name("테스트 그룹")
                        .place("none")
                        .date(LocalDate.of(2023, 7, 23))
                        .build()
        );

        GroupRequest.Participate request =
                new GroupRequest.Participate(saveGroup.getGroupId(), "경기도불주먹", 37.5660, 126.9784, "BUS", "2345");

        // when
        GroupResponse.Participate response =
                groupService.participateGroup(request.toServiceRequest(), participateUser);

        // then
        assertThat(response.getParticipationId()).isNotNull();
        assertThat(response)
                .extracting(
                        "groupId", "userId",
                        "userName", "latitude", "longitude",
                        "transportation"
                )
                .contains(
                        saveGroup.getGroupId(), participateUser.getUserId(), "경기도불주먹",
                        37.5660, 126.9784, "BUS"
                );

        Participation participation = participationRepository.findById(response.getParticipationId()).get();
        assertThat(participation.getPassword()).isEqualTo(encrypt("2345"));
    }

    @DisplayName("하나의 유저가 하나의 그룹에 참가할때 비밀번호를 입력하지 않는다.")
    @Test
    void participateNotEnteredPassword() {
        // given
        Users user = userRepository.save(
                Users.builder()
                        .email("test@test.com")
                        .name("테스트 이름")
                        .build()
        );

        Users participateUser = userRepository.save(
                Users.builder()
                        .email("test2@test.com")
                        .name("테스트 이름2")
                        .build()
        );

        Groups saveGroup = groupRepository.save(
                Groups.builder()
                        .adminId(user.getUserId())
                        .name("테스트 그룹")
                        .place("none")
                        .date(LocalDate.of(2023, 7, 23))
                        .build()
        );

        GroupRequest.Participate request =
                new GroupRequest.Participate(saveGroup.getGroupId(), "경기도불주먹", 37.5660, 126.9784, "BUS", null);

        // when
        GroupResponse.Participate response =
                groupService.participateGroup(request.toServiceRequest(), participateUser);

        // then
        assertThat(response)
                .extracting(
                        "participationId", "groupId", "userId",
                        "userName", "latitude", "longitude",
                        "transportation"
                )
                .contains(
                        1L, saveGroup.getGroupId(), participateUser.getUserId(),
                        "경기도불주먹", 37.5660, 126.9784,
                        "BUS"
                );

        Participation participation = participationRepository.findById(response.getParticipationId()).get();
        assertThat(participation.getPassword()).isNull();
    }

    @DisplayName("하나의 유저가 하나의 그룹에 참가할때 이동수단이 BUS, SUBWAY 가 아닌 잘못된 코드를 보내면 Exception 이 발생한다.")
    @Test
    void throwsExceptionWhenParticipateWithInvalidTransportation() {
        // given
        Groups saveGroup = groupRepository.save(
                Groups.builder()
                        .adminId(1L)
                        .name("모임장")
                        .place("none")
                        .date(LocalDate.of(2023, 7, 12))
                        .build()
        );

        Users user = userRepository.save(
                Users.builder()
                        .email("test@test.com")
                        .name("테스트 이름")
                        .build()
        );

        GroupRequest.Participate request
                = new GroupRequest.Participate(saveGroup.getGroupId(), "경기도불주먹", 37.5660, 126.9784, "TAXI", "abc123");

        // when // then
        assertThatThrownBy(() -> groupService.participateGroup(request.toServiceRequest(), user))
                .extracting("result.code", "result.message")
                .contains(-1002, "잘못된 이동수단 입니다.");
    }

    // method
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