package com.moim.backend.domain.space.controller;

import com.moim.backend.domain.space.request.GroupRequest;
import com.moim.backend.domain.space.response.GroupResponse;
import com.moim.backend.domain.space.service.GroupService;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.auth.Login;
import com.moim.backend.global.common.CustomResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    // 모임 생성
    @PostMapping("/api/v1/group")
    public CustomResponseEntity<GroupResponse.Create> createGroup(
            @RequestBody @Valid GroupRequest.Create request, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.createGroup(request.toServiceRequest(), user));
    }

    // 모임 참여
    @PostMapping("/api/v1/group/participate")
    public CustomResponseEntity<GroupResponse.Participate> participateGroup(
            @RequestBody @Valid GroupRequest.Participate request, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateGroup(request.toServiceRequest(), user));
    }

    // 모임 참여 정보 수정
    @PatchMapping("/api/v1/group/participate")
    public CustomResponseEntity<GroupResponse.ParticipateUpdate> participateUpdate(
            @RequestBody @Valid GroupRequest.ParticipateUpdate request, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateUpdate(request.toServiceRequest(), user));
    }

    // 모임 나가기
    @DeleteMapping("/api/v1/group/participate")
    public CustomResponseEntity<GroupResponse.Exit> participateExit(
            @RequestParam Long participateId, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateExit(participateId, user));
    }

    // 모임원 내보내기 (Admin)
    @DeleteMapping("/api/v1/group/participate/removal")
    public CustomResponseEntity<Void> participateRemoval(
            @RequestParam Long participateId, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateRemoval(participateId, user));
    }
}
