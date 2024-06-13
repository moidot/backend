package com.moim.backend.domain.space.controller;

import com.moim.backend.domain.space.request.CreateSpaceCalendarRequest;
import com.moim.backend.domain.space.request.SpaceTimeLineRequest;
import com.moim.backend.domain.space.response.space.CreateSpaceCalendarResponse;
import com.moim.backend.domain.space.response.space.SpaceTimeLineResponse;
import com.moim.backend.domain.space.service.SpaceCalendarService;
import com.moim.backend.global.common.CustomResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/space")
public class SpaceCalendarController {

    private final SpaceCalendarService spaceCalendarService;

    // 모임 캘린더 일정 추가 API
    @PostMapping("/calendar")
    public CustomResponseEntity<CreateSpaceCalendarResponse> createSpaceCalendar(
            @RequestBody @Valid CreateSpaceCalendarRequest request
    ) {
        return CustomResponseEntity.success(spaceCalendarService.createSpaceCalendar(request));
    }

    // 타임라인 조회 API
    @GetMapping("/timeLine")
    public CustomResponseEntity<SpaceTimeLineResponse> readSpaceTimeLine(
            @RequestBody @Valid SpaceTimeLineRequest request
    ) {
        return CustomResponseEntity.success(spaceCalendarService.readSpaceTimeLine(request));
    }
}
