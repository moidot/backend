package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.SpaceCalendar;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.space.repository.SpaceCalendarRepository;
import com.moim.backend.domain.space.repository.SpaceRepository;
import com.moim.backend.domain.space.request.CreateSpaceCalendarRequest;
import com.moim.backend.domain.space.request.SpaceTimeLineRequest;
import com.moim.backend.domain.space.response.space.CreateSpaceCalendarResponse;
import com.moim.backend.domain.space.response.space.SpaceTimeLineResponse;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
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
import java.util.Map;

import static com.moim.backend.domain.space.entity.TransportationType.*;
import static com.moim.backend.domain.space.response.space.SpaceTimeLineResponse.*;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SpaceCalendarServiceTest {

    @Autowired
    SpaceCalendarService spaceCalendarService;

    @Autowired
    SpaceRepository spaceRepository;

    @Autowired
    ParticipationRepository participationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SpaceCalendarRepository spaceCalendarRepository;

    @Autowired
    EntityManager em;

    @DisplayName("모임 일정을 생성한다.")
    @Test
    void createSpaceCalendar() {
        // given
        Users user = savedUser("abcd@gmail.com", "abcd");
        Space space = savedSpace(user.getUserId(), "back test");
        Participation participation = savedParticipation(user, space, "백테스트", "안산 어쩌구", 126.213812, 32.329421, PERSONAL);

        LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 2, 15, 37, 32);
        CreateSpaceCalendarRequest request =
                new CreateSpaceCalendarRequest(space.getSpaceId(), "테스트 입니다", localDateTime, "테스트", "안산시 성포동");

        // when
        CreateSpaceCalendarResponse response = spaceCalendarService.createSpaceCalendar(request);

        // then
        assertThat(response)
                .extracting("spaceId", "scheduleName", "date", "dayOfWeek", "note", "locationName")
                .contains(space.getSpaceId(), "테스트 입니다", "2024-01-02T15:37:32", "화", "테스트", "안산시 성포동");
    }

    @DisplayName("모임의 타임라인을 확인한다.")
    @Test
    void readSpaceTimeLine() {
        // given
        Users user = savedUser("abcd@gmail.com", "abcd");
        Space space = savedSpace(user.getUserId(), "back test");
        Participation participation = savedParticipation(user, space, "백테스트", "안산 어쩌구", 126.213812, 32.329421, PERSONAL);

        savedSpaceSchedule(space, "모이닷 회의", LocalDateTime.of(2024, 2, 2, 22, 0, 0));
        savedSpaceSchedule(space, "백엔드 작업", LocalDateTime.of(2024, 2, 5, 15, 0, 0));
        savedSpaceSchedule(space, "디자인 회의", LocalDateTime.of(2024, 2, 9, 18, 0, 0));
        savedSpaceSchedule(space, "모이닷 회의", LocalDateTime.of(2024, 2, 9, 22, 0, 0));
        savedSpaceSchedule(space, "프론트 회의", LocalDateTime.of(2024, 2, 9, 23, 59, 59));
        savedSpaceSchedule(space, "모이닷 회의", LocalDateTime.of(2024, 2, 16, 22, 0, 0));
        savedSpaceSchedule(space, "프론트 작업", LocalDateTime.of(2024, 2, 21, 22, 0, 0));
        savedSpaceSchedule(space, "모이닷 회의", LocalDateTime.of(2024, 2, 23, 22, 0, 0));
        savedSpaceSchedule(space, "모이닷 회의", LocalDateTime.of(2024, 2, 29, 22, 0, 0));
        savedSpaceSchedule(space, "백엔드 마무리", LocalDateTime.of(2024, 2, 29, 23, 59, 59));

        em.flush();
        em.clear();

        SpaceTimeLineRequest request = new SpaceTimeLineRequest(
                space.getSpaceId(), LocalDateTime.of(2024, 2, 1, 0, 0, 0)
        );

        // when
        SpaceTimeLineResponse spaceTimeLineResponse = spaceCalendarService.readSpaceTimeLine(request);

        // then
        Map<String, List<Schedule>> timeLine = spaceTimeLineResponse.getTimeLine();

        assertThat(timeLine)
                .hasSize(29);

        assertThat(timeLine.get("1/목"))
                .hasSize(0);

        assertThat(timeLine.get("2/금"))
                .hasSize(1)
                .extracting("scheduleName", "date")
                .contains(
                        tuple("모이닷 회의", "오후 10:00")
                );

        assertThat(timeLine.get("9/금"))
                .hasSize(3)
                .extracting("scheduleName", "date")
                .contains(
                        tuple("디자인 회의", "오후 6:00"),
                        tuple("모이닷 회의", "오후 10:00"),
                        tuple("프론트 회의", "오후 11:59")
                );

        assertThat(timeLine.get("29/목"))
                .hasSize(2)
                .extracting("scheduleName", "date")
                .contains(
                        tuple("모이닷 회의", "오후 10:00"),
                        tuple("백엔드 마무리", "오후 11:59")
                );
    }

    private void savedSpaceSchedule(Space space, String scheduleName, LocalDateTime date) {
        spaceCalendarRepository.save(SpaceCalendar.builder()
                .space(space)
                .scheduleName(scheduleName)
                .date(date)
                .dayOfWeek(date.getDayOfWeek().getDisplayName(SHORT, KOREAN))
                .note("테스트")
                .build()
        );
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