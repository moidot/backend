package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.space.repository.SpaceRepository;
import com.moim.backend.domain.space.request.controller.CreateSpaceCalendarRequest;
import com.moim.backend.domain.space.response.space.CreateSpaceCalendarResponse;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.moim.backend.domain.space.entity.TransportationType.*;
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
}