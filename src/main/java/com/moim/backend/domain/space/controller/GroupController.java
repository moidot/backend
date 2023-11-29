package com.moim.backend.domain.space.controller;

import com.moim.backend.domain.space.request.controller.GroupCreateRequest;
import com.moim.backend.domain.space.request.controller.GroupNameUpdateRequest;
import com.moim.backend.domain.space.request.controller.GroupParticipateRequest;
import com.moim.backend.domain.space.request.controller.GroupParticipateUpdateRequest;
import com.moim.backend.domain.space.response.*;
import com.moim.backend.domain.space.response.group.*;
import com.moim.backend.domain.space.service.GroupService;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.auth.Login;
import com.moim.backend.global.common.CustomResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    // 모임 생성 API
    @PostMapping("")
    public CustomResponseEntity<GroupCreateResponse> createGroup(
            @RequestBody @Valid GroupCreateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.createGroup(request.toServiceRequest(), user));
    }

    // 모임 참여자 정보 리스트 조회 API
    @GetMapping("")
    public CustomResponseEntity<GroupDetailResponse> readParticipateGroupByRegion(
            @RequestParam Long groupId
    ) {
        return CustomResponseEntity.success(groupService.readParticipateGroupByRegion(groupId));
    }

    // 모임 이름 수정 API
    @PatchMapping("")
    public CustomResponseEntity<Void> updateGroupName(
            @RequestParam Long groupId,
            @RequestBody @Valid GroupNameUpdateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(
                groupService.updateGroupName(groupId,request.toServiceRequest(), user)
        );
    }

    // 모임 참여 API
    @PostMapping("/participate")
    public CustomResponseEntity<GroupParticipateResponse> participateGroup(
            @RequestBody @Valid GroupParticipateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateGroup(request.toServiceRequest(), user));
    }

    // 모임 삭제 API
    @DeleteMapping("")
    public CustomResponseEntity<Void> deleteGroup(
            @RequestParam Long groupId, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateDelete(groupId, user));
    }

    // 내 참여 정보 수정 API
    @PatchMapping("/participate")
    public CustomResponseEntity<GroupParticipateUpdateResponse> participateUpdate(
            @RequestBody @Valid GroupParticipateUpdateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateUpdate(request.toServiceRequest(), user));
    }

    // 모임 나가기 API
    @DeleteMapping("/participate")
    public CustomResponseEntity<GroupExitResponse> participateExit(
            @RequestParam Long participateId, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateExit(participateId, user));
    }

    // 모임원 내보내기 API
    @DeleteMapping("/participate/removal")
    public CustomResponseEntity<Void> participateRemoval(
            @RequestParam Long participateId, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateRemoval(participateId, user));
    }

    // 모임 추천 역(랜드마크) 조회하기 API
    @GetMapping("/best-region")
    public CustomResponseEntity<List<PlaceRouteResponse>> getBestRegion(
            @RequestParam Long groupId
    ) {
        return CustomResponseEntity.success(groupService.getBestRegion(groupId));
    }

    // 내 모임 확인하기 API
    @GetMapping("/participate")
    public CustomResponseEntity<List<GroupMyParticipateResponse>> getMyParticipate(
            @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.getMyParticipate(user));
    }

    // 모임 장소 추천 조회 리스트 API
    @GetMapping("/best-region/place")
    public CustomResponseEntity<List<GroupPlaceResponse>> keywordCentralizedMeetingSpot(
            @RequestParam Double x,
            @RequestParam Double y,
            @RequestParam String local,
            @RequestParam String keyword
    ) {
        return CustomResponseEntity.success(groupService.keywordCentralizedMeetingSpot(x, y, local, keyword));
    }
}
