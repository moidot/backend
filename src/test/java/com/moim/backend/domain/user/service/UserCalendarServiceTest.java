package com.moim.backend.domain.user.service;

import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.SpaceCalendar;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.space.repository.SpaceCalendarRepository;
import com.moim.backend.domain.space.repository.SpaceRepository;
import com.moim.backend.domain.user.entity.UserCalendar;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserCalendarRepository;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.domain.user.request.CreateUserCalendarRequest;
import com.moim.backend.domain.user.request.UserCalendarPageRequest;
import com.moim.backend.domain.user.request.UserDetailCalendarRequest;
import com.moim.backend.domain.user.response.CreateUserCalendarResponse;
import com.moim.backend.domain.user.response.UserCalendarPageResponse;
import com.moim.backend.domain.user.response.UserDetailCalendarResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.moim.backend.domain.space.entity.TransportationType.PERSONAL;
import static com.moim.backend.domain.space.entity.TransportationType.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserCalendarServiceTest {

    @Autowired
    private UserCalendarService userCalendarService;

    @Autowired
    private UserCalendarRepository userCalendarRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private SpaceCalendarRepository spaceCalendarRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("유저가 자신의 일정을 하나 추가한다.")
    @Test
    void createUserCalendar() {
        // given
        Users user = savedUser("moidot@gmail.com", "모이닷주인장");
        CreateUserCalendarRequest request = new CreateUserCalendarRequest("모이닷 모임", LocalDateTime.of(2024, 1, 24, 15, 38, 15), "메모", "종로 2가", "R");

        // when
        CreateUserCalendarResponse response = userCalendarService.createUserCalendar(user, request);

        // then
        assertThat(response)
                .extracting("userId", "scheduleName", "date", "dayOfWeek", "note", "locationName", "color")
                .contains(user.getUserId(), "모이닷 모임", "2024-01-24T15:38:15", "수", "메모", "종로 2가", "R");
    }

    @DisplayName("해당 날짜 캘린더 조회")
    @Test
    void readCalendar() {
        // given
        Users user = savedUser("back@gmail.com", "천백엔드");
        Space space = savedSpace(user.getUserId(), "캘린더 테스트용");
        Participation participation = savedParticipation(user, space, "천백", "경기 양주시 부흥로", 126.123456, 37.123123, PUBLIC);

        savedCalendar(space, LocalDateTime.of(2024, 2, 7, 13, 23, 30));
        savedCalendar(space, LocalDateTime.of(2024, 2, 9, 13, 23, 30));
        savedCalendar(space, LocalDateTime.of(2024, 2, 13, 13, 23, 30));
        savedCalendar(space, LocalDateTime.of(2024, 2, 26, 13, 23, 30));
        savedCalendar(space, LocalDateTime.of(2024, 2, 29, 13, 23, 30));

        Space space2 = savedSpace(user.getUserId(), "캘린더 테스트용2");
        Participation participation2 = savedParticipation(user, space, "천백2", "경기 양주시 부흥로", 126.123456, 37.123123, PUBLIC);

        savedCalendar(space2, LocalDateTime.of(2024, 2, 1, 22, 30, 0));
        savedCalendar(space2, LocalDateTime.of(2024, 2, 9, 19, 0, 0));
        savedCalendar(space2, LocalDateTime.of(2024, 2, 13, 22, 30, 0));

        savedMyCalendar(user, LocalDateTime.of(2024, 2, 9, 13, 23, 30));
        savedMyCalendar(user, LocalDateTime.of(2024, 2, 9, 22, 0, 30), "치킨 주문하기");
        savedMyCalendar(user, LocalDateTime.of(2024, 2, 17, 13, 23, 30));
        savedMyCalendar(user, LocalDateTime.of(2024, 2, 21, 13, 23, 30));

        UserCalendarPageRequest request = new UserCalendarPageRequest(LocalDateTime.of(2024, 2, 1, 0, 0, 0));

        // when
        UserCalendarPageResponse response = userCalendarService.readMyCalendar(user, request);

        // then
        assertThat(response.getCalendar())
                .hasSize(31);

        assertThat(response.getCalendar().get(9))
                .extracting("color", "scheduleName")
                .contains(
                        tuple("RED", "엄마랑 데이트"),
                        tuple("RED", "치킨 주문하기"),
                        tuple("space", "모이닷 회의"),
                        tuple("space", "모이닷 회의")
                );
    }

    @DisplayName("해당 날짜의 내 전체 일정을 확인한다.")
    @Test
    void readDetailCalendar() {
        // given
        Users user = savedUser("백1@gmail.com", "백1");
        Space space = savedSpace(user.getUserId(), "스1");
        savedParticipation(user, space, "백1", "test test", 123.123456, 37.123456, PERSONAL);

        savedMyCalendar(user, LocalDateTime.of(2024, 2, 9, 0, 0, 0));
        savedCalendar(space, LocalDateTime.of(2024, 2, 9, 23, 59, 59));

        UserDetailCalendarRequest request = new UserDetailCalendarRequest(LocalDateTime.of(2024, 2, 9, 0, 0, 0));

        // when
        List<UserDetailCalendarResponse> response = userCalendarService.readDetailCalendar(user, request);

        // then
        assertThat(response)
                .extracting("spaceName", "scheduleName", "date", "color")
                .contains(
                        tuple("", "엄마랑 데이트", "2024.02.09(금) 00:00", "RED"),
                        tuple("스1", "모이닷 회의", "2024.02.09(금) 23:59", "space")
                );
    }


    private void savedMyCalendar(Users user, LocalDateTime date) {
        userCalendarRepository.save(
                UserCalendar.builder()
                        .user(user)
                        .scheduleName("엄마랑 데이트")
                        .date(date)
                        .dayOfWeek("테스트")
                        .note("테스트")
                        .color("RED")
                        .build()
        );
    }

    private void savedMyCalendar(Users user, LocalDateTime date, String scheduleName) {
        userCalendarRepository.save(
                UserCalendar.builder()
                        .user(user)
                        .scheduleName(scheduleName)
                        .date(date)
                        .dayOfWeek("테스트")
                        .note("테스트")
                        .color("RED")
                        .build()
        );
    }

    private void savedCalendar(Space space, LocalDateTime localDateTime) {
        spaceCalendarRepository.save(SpaceCalendar.builder()
                .space(space)
                .scheduleName("모이닷 회의")
                .date(localDateTime)
                .dayOfWeek("테스트")
                .note("테스트")
                .build());
    }

    private Space savedSpace(Long userId, String name) {
        return spaceRepository.save(
                Space.builder()
                        .adminId(userId)
                        .name(name)
                        .place("none")
                        .date(LocalDate.of(2023, 7, 10))
                        .build()
        );
    }

    private Participation savedParticipation(
            Users user, Space group, String userName,
            String locationName, Double latitude, Double longitude,
            TransportationType transportationType
    ) {
        return participationRepository.save(
                Participation.builder()
                        .space(group)
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

}