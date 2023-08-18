package com.moim.backend.domain.space.controller;

import com.moim.backend.domain.space.request.GroupRequest;
import com.moim.backend.domain.space.response.GroupResponse;
import com.moim.backend.domain.space.service.GroupService;
import com.moim.backend.domain.subway.response.BestSubwayInterface;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.auth.Login;
import com.moim.backend.global.common.CustomResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
public class GroupController {

    private final GroupService groupService;

    // 모임 생성하기
    @PostMapping("")
    public CustomResponseEntity<GroupResponse.Create> createGroup(
            @RequestBody @Valid GroupRequest.Create request, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.createGroup(request.toServiceRequest(), user));
    }

    // 모임 참여자 정보 리스트 조회 API
    @GetMapping("")
    public CustomResponseEntity<GroupResponse.Detail> readParticipateGroupByRegion(@RequestParam Long groupId) {
        return CustomResponseEntity.success(groupService.readParticipateGroupByRegion(groupId));
    }


    // 모임 참여하기
    @PostMapping("/participate")
    public CustomResponseEntity<GroupResponse.Participate> participateGroup(
            @RequestBody @Valid GroupRequest.Participate request, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateGroup(request.toServiceRequest(), user));
    }

    // 모임 삭제하기
    @DeleteMapping("")
    public CustomResponseEntity<Void> deleteGroup(
            @RequestParam Long groupId, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateDelete(groupId, user));
    }

    // 모임 참여 정보 수정
    @PatchMapping("/participate")
    public CustomResponseEntity<GroupResponse.ParticipateUpdate> participateUpdate(
            @RequestBody @Valid GroupRequest.ParticipateUpdate request, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateUpdate(request.toServiceRequest(), user));
    }

    // 모임 나가기
    @DeleteMapping("/participate")
    public CustomResponseEntity<GroupResponse.Exit> participateExit(
            @RequestParam Long participateId, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateExit(participateId, user));
    }

    // 모임원 내보내기 (Admin)
    @DeleteMapping("/participate/removal")
    public CustomResponseEntity<Void> participateRemoval(
            @RequestParam Long participateId, @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.participateRemoval(participateId, user));
    }

    // 모임 추천 지역 조회하기
    @GetMapping("/best-region")
    public CustomResponseEntity<List<BestSubwayInterface>> getBestRegion(
            @RequestParam Long groupId
    ) {
        return CustomResponseEntity.success(groupService.getBestRegion(groupId));
    }

    // 내 모임 확인하기
    @GetMapping("/participate")
    public CustomResponseEntity<List<GroupResponse.MyParticipate>> getMyParticipate(
            @Login Users user
    ) {
        return CustomResponseEntity.success(groupService.getMyParticipate(user));
    }

    // 모임 장소 추천 조회 리스트 API
    @GetMapping("/best-region/place")
    public CustomResponseEntity<List<GroupResponse.Place>> keywordCentralizedMeetingSpot(
            @RequestParam Double x,
            @RequestParam Double y,
            @RequestParam String local,
            @RequestParam String keyword
    ) {
        return CustomResponseEntity.success(groupService.keywordCentralizedMeetingSpot(x, y, local, keyword));
    }
}
