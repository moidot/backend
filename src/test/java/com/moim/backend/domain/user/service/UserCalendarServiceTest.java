package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.domain.user.request.CreateUserCalendarRequest;
import com.moim.backend.domain.user.response.CreateUserCalendarResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserCalendarServiceTest {

    @Autowired
    private UserCalendarService userCalendarService;

    @Autowired
    private UserRepository userRepository;

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

    private Users savedUser(String email, String name) {
        return userRepository.save(
                Users.builder()
                        .email(email)
                        .name(name)
                        .build()
        );
    }
}