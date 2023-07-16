package com.moim.backend.domain.space.controller;

import com.moim.backend.domain.space.request.GroupRequest;
import com.moim.backend.domain.space.response.GroupResponse;
import com.moim.backend.domain.space.service.GroupService;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.auth.Login;
import com.moim.backend.global.common.CustomResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/api/v1/group")
    public CustomResponseEntity<GroupResponse.Create> createGroup(
            @RequestBody @Valid GroupRequest.Create request, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.createGroup(request.toServiceRequest(), user));
    }

    @PostMapping("/api/v1/group/participate")
    public CustomResponseEntity<GroupResponse.Participate> participateGroup(
            @RequestBody @Valid GroupRequest.Participate request, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateGroup(request.toServiceRequest(), user));
    }
}
