package com.moim.backend.domain.space.controller;

import com.moim.backend.domain.space.request.controller.CreateSpaceCalendarRequest;
import com.moim.backend.domain.space.response.space.CreateSpaceCalendarResponse;
import com.moim.backend.domain.space.service.SpaceCalendarService;
import com.moim.backend.global.common.CustomResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/space")
public class SpaceCalendarController {

    private final SpaceCalendarService spaceCalendarService;

    @PostMapping("/calendar")
    public CustomResponseEntity<CreateSpaceCalendarResponse> createSpaceCalendar(
            @RequestBody @Valid CreateSpaceCalendarRequest request
    ) {
        return CustomResponseEntity.success(spaceCalendarService.createSpaceCalendar(request));
    }
}
