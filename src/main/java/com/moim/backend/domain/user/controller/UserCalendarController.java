package com.moim.backend.domain.user.controller;

import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.request.CreateUserCalendarRequest;
import com.moim.backend.domain.user.response.CreateUserCalendarResponse;
import com.moim.backend.domain.user.service.UserCalendarService;
import com.moim.backend.global.auth.Login;
import com.moim.backend.global.common.CustomResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserCalendarController {
    private final UserCalendarService userCalendarService;

    @PostMapping("/calendar")
    public CustomResponseEntity<CreateUserCalendarResponse> createUserCalendar(
            @Login Users user,
            // 안드와 날짜 요청에 대한 값 회의 필요
            @RequestBody @Valid CreateUserCalendarRequest createUserCalendarRequest
    ) {
        return CustomResponseEntity.success(userCalendarService.createUserCalendar(user, createUserCalendarRequest));
    }
}
