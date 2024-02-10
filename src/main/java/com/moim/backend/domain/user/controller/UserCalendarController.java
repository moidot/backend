package com.moim.backend.domain.user.controller;

import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.request.CreateUserCalendarRequest;
import com.moim.backend.domain.user.request.UserCalendarPageRequest;
import com.moim.backend.domain.user.request.UserDetailCalendarRequest;
import com.moim.backend.domain.user.response.CreateUserCalendarResponse;
import com.moim.backend.domain.user.response.UserCalendarPageResponse;
import com.moim.backend.domain.user.response.UserDetailCalendarResponse;
import com.moim.backend.domain.user.service.UserCalendarService;
import com.moim.backend.global.auth.Login;
import com.moim.backend.global.common.CustomResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/calendar")
    public CustomResponseEntity<UserCalendarPageResponse> readCalendar(
            @Login Users user,
            @RequestBody @Valid UserCalendarPageRequest request
    ) {
        return CustomResponseEntity.success(userCalendarService.readMyCalendar(user, request));
    }

    @GetMapping("/calendar/detail")
    public CustomResponseEntity<List<UserDetailCalendarResponse>> readDetailCalendar(
            @Login Users user,
            @RequestBody @Valid UserDetailCalendarRequest request
    ) {
        return CustomResponseEntity.success(userCalendarService.readDetailCalendar(user, request));
    }
}
