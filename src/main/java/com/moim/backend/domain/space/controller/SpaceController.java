package com.moim.backend.domain.space.controller;

import com.moim.backend.domain.space.request.SpaceCreateRequest;
import com.moim.backend.domain.space.request.SpaceNameUpdateRequest;
import com.moim.backend.domain.space.request.SpaceParticipateRequest;
import com.moim.backend.domain.space.request.SpaceParticipateUpdateRequest;
import com.moim.backend.domain.space.response.NicknameValidationResponse;
import com.moim.backend.domain.space.response.PlaceRouteResponse;
import com.moim.backend.domain.space.response.SpaceFilterEnum;
import com.moim.backend.domain.space.response.space.*;
import com.moim.backend.domain.space.service.SpaceService;
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
public class SpaceController {

    private final SpaceService spaceService;

    // 모임 생성 API
    @PostMapping("")
    public CustomResponseEntity<SpaceCreateResponse> createSpace(
            @RequestBody @Valid SpaceCreateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.createSpace(request, user));
    }

    // 모임 참여자 정보 리스트 조회 API
    @GetMapping("")
    public CustomResponseEntity<SpaceDetailResponse> readParticipateSpaceByRegion(
            @RequestParam Long groupId
    ) {
        return CustomResponseEntity.success(spaceService.readParticipateSpaceByRegion(groupId));
    }

    // 모임 이름 수정 API
    @PatchMapping("")
    public CustomResponseEntity<Void> updateSpaceName(
            @RequestParam Long groupId,
            @RequestBody @Valid SpaceNameUpdateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.updateSpaceName(groupId, request, user));
    }

    // 모임 참여 API
    @PostMapping("/participate")
    public CustomResponseEntity<SpaceParticipateResponse> participateSpace(
            @RequestBody @Valid SpaceParticipateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.participateSpace(request, user));
    }

    // 모임 삭제 API
    @DeleteMapping("")
    public CustomResponseEntity<Void> deleteSpace(
            @RequestParam Long groupId, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.participateDelete(groupId, user));
    }

    // 내 참여 정보 조회
    @GetMapping("/user")
    public CustomResponseEntity<SpaceParticipationsResponse> getParticipationDetail(
            @RequestParam Long groupId, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.getParticipationDetail(groupId, user));
    }


    // 내 참여 정보 수정 API
    @PatchMapping("/participate")
    public CustomResponseEntity<SpaceParticipateUpdateResponse> participateUpdate(
            @RequestBody @Valid SpaceParticipateUpdateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.participateUpdate(request, user));
    }

    // 모임 나가기 API
    @DeleteMapping("/participate")
    public CustomResponseEntity<SpaceExitResponse> participateExit(
            @RequestParam Long participateId, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.participateExit(participateId, user));
    }

    // 모임 전체 나가기 API
    @DeleteMapping("/participate/all")
    public CustomResponseEntity<Void> allParticipateExit(
            @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.allParticipateExit(user));
    }

    // 모임원 내보내기 API
    @DeleteMapping("/participate/removal")
    public CustomResponseEntity<Void> participateRemoval(
            @RequestParam Long participateId, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.participateRemoval(participateId, user));
    }

    // 모임 추천 역(랜드마크) 조회하기 API
    @GetMapping("/best-region")
    public CustomResponseEntity<List<PlaceRouteResponse>> getBestRegion(
            @RequestParam Long groupId
    ) {
        return CustomResponseEntity.success(spaceService.getBestRegion(groupId));
    }

    // 내 모임 확인하기 API
    @GetMapping("/participate")
    public CustomResponseEntity<List<SpaceMyParticipateResponse>> getMyParticipate(
            @Login Users user,
            @RequestParam(required = false) String spaceName,
            @RequestParam(required = false) SpaceFilterEnum filter
    ) {
        return CustomResponseEntity.success(spaceService.getMyParticipate(user, spaceName, filter));
    }

    // 모임 장소 추천 조회 리스트 API
    @GetMapping("/best-region/place")
    public CustomResponseEntity<List<SpacePlaceResponse>> keywordCentralizedMeetingSpot(
            @RequestParam Double x,
            @RequestParam Double y,
            @RequestParam String local,
            @RequestParam String keyword
    ) {
        return CustomResponseEntity.success(spaceService.keywordCentralizedMeetingSpot(x, y, local, keyword));
    }

    // 닉네임 유효성 체크
    @GetMapping("/nickname")
    public CustomResponseEntity<NicknameValidationResponse> checkNicknameValidation(
            @RequestParam Long groupId,
            @RequestParam String nickname
    ) {
        return CustomResponseEntity.success(spaceService.checkNicknameValidation(groupId, nickname));
    }
}
