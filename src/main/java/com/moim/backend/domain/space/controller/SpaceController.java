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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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
    @Operation(summary = "모임 생성", description = "모임 생성")
    public CustomResponseEntity<SpaceCreateResponse> createSpace(
            @RequestBody @Valid SpaceCreateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.createSpace(request, user));
    }

    // 모임 참여자 정보 리스트 조회 API
    @GetMapping("")
    @Operation(summary = "모임 참여자 정보 리스트 조회", description = "모임에 참여하고 있는 모임원 조회")
    public CustomResponseEntity<SpaceDetailResponse> readParticipateSpaceByRegion(
            @RequestParam Long groupId
    ) {
        return CustomResponseEntity.success(spaceService.readParticipateSpaceByRegion(groupId));
    }

    // 모임 이름 수정 API
    @PatchMapping("")
    @Operation(summary = "모임 이름 수정", description = "모임 이름 수정")
    public CustomResponseEntity<Void> updateSpaceName(
            @RequestParam Long groupId,
            @RequestBody @Valid SpaceNameUpdateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.updateSpaceName(groupId, request, user));
    }

    // 모임 참여 API
    @PostMapping("/participate")
    @Operation(summary = "모임 참여", description = "모임 참여")
    public CustomResponseEntity<SpaceParticipateResponse> participateSpace(
            @RequestBody @Valid SpaceParticipateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.participateSpace(request, user));
    }

    // 모임 삭제 API
    @DeleteMapping("")
    @Operation(summary = "모임 삭", description = "모임 삭제")
    public CustomResponseEntity<Void> deleteSpace(
            @RequestParam Long groupId, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.participateDelete(groupId, user));
    }

    // 내 참여 정보 조회
    @GetMapping("/user")
    @Operation(summary = "내 참여 정보 조회", description = "내 참여 정보 조회")
    @Parameters({
            @Parameter(name = "groupId", description = "스페이스 아이디", example = "8")
    })
    public CustomResponseEntity<SpaceParticipationsResponse> getParticipationDetail(
            @RequestParam Long groupId, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.getParticipationDetail(groupId, user));
    }


    // 내 참여 정보 수정 API
    @PatchMapping("/participate")
    @Operation(summary = "내 참여 정보 수정", description = "내 참여 정보 수정")
    public CustomResponseEntity<SpaceParticipateUpdateResponse> participateUpdate(
            @RequestBody @Valid SpaceParticipateUpdateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.participateUpdate(request, user));
    }

    // 모임 나가기 API
    @DeleteMapping("/participate")
    @Operation(summary = "모임 나가기", description = "모임 나가기")
    public CustomResponseEntity<SpaceExitResponse> participateExit(
            @RequestParam Long participateId, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.participateExit(participateId, user));
    }

    // 모임 전체 나가기 API
    @DeleteMapping("/participate/all")
    @Operation(summary = "모임 전체 나가기", description = "모임 전체 나가기")
    public CustomResponseEntity<Void> allParticipateExit(
            @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.allParticipateExit(user));
    }

    // 모임원 내보내기 API
    @DeleteMapping("/participate/removal")
    @Operation(summary = "모임원 내보내기", description = "모임원 내보내기")
    public CustomResponseEntity<Void> participateRemoval(
            @RequestParam Long participateId, @Login Users user
    ) {
        return CustomResponseEntity.success(spaceService.participateRemoval(participateId, user));
    }

    // 모임 추천 역(랜드마크) 조회하기 API
    @GetMapping("/best-region")
    @Operation(summary = "모임 추천 지역 조회", description = "중간 지역 추천")
    public CustomResponseEntity<List<PlaceRouteResponse>> getBestRegion(
            @RequestParam Long groupId
    ) {
        return CustomResponseEntity.success(spaceService.getBestRegionWithTmap(groupId));
    }

    // 내 모임 확인하기 API
    @GetMapping("/participate")
    @Operation(summary = "내 모임 리스트 조회", description = "내가 참여하고 있는 모임 리스트")
    public CustomResponseEntity<List<SpaceMyParticipateResponse>> getMyParticipate(
            @Login Users user,
            @RequestParam(required = false) String spaceName,
            @RequestParam(required = false) SpaceFilterEnum filter
    ) {
        return CustomResponseEntity.success(spaceService.getMyParticipate(user, spaceName, filter));
    }

    // 추천 모임 장소 리스트 조회 API
    @GetMapping("/best-region/place")
    @Operation(summary = "추천 모임 장소 리스트 조회", description = "추천 지역 근처 모임 장소 추천")
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
    @Operation(summary = "닉네임 유효성 체크", description = "스페이스 내에 동일한 닉네임이 있는지 중복 체크")
    public CustomResponseEntity<NicknameValidationResponse> checkNicknameValidation(
            @RequestParam Long groupId,
            @RequestParam String nickname
    ) {
        return CustomResponseEntity.success(spaceService.checkNicknameValidation(groupId, nickname));
    }
}
