package com.moim.backend.domain.spacevote.controller;

import com.moim.backend.domain.spacevote.request.controller.VoteCreateRequest;
import com.moim.backend.domain.spacevote.response.VoteCreateResponse;
import com.moim.backend.domain.spacevote.response.VoteSelectPlaceUserResponse;
import com.moim.backend.domain.spacevote.response.VoteSelectResultResponse;
import com.moim.backend.domain.spacevote.service.VoteService;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.auth.Login;
import com.moim.backend.global.common.CustomResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class VoteController {
    private final VoteService voteService;

    // 투표 생성 API
    @PostMapping("/{groupId}/vote")
    public CustomResponseEntity<VoteCreateResponse> createVote(
            @PathVariable Long groupId,
            @RequestBody @Valid VoteCreateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(voteService.createVote(request, groupId, user));
    }

    // 투표 읽기 API
    @GetMapping("/{groupId}/vote")
    public CustomResponseEntity<VoteSelectResultResponse> readVote(
            @PathVariable Long groupId, @Login Users user
    ) {
        return CustomResponseEntity.success(voteService.readVote(groupId, user));
    }

    // 투표 참여 API
    @PostMapping("/{groupId}/vote/select")
    public CustomResponseEntity<VoteSelectResultResponse> selectVote(
            @PathVariable Long groupId, @RequestParam List<Long> bestPlaceIds, @Login Users user
    ) {
        return CustomResponseEntity.success(
                voteService.selectVote(groupId, bestPlaceIds, user, LocalDateTime.now())
        );
    }

    // 해당 장소 투표한 인원 리스트 조회하기 API
    @GetMapping("/{groupId}/vote/select")
    public CustomResponseEntity<VoteSelectPlaceUserResponse> readSelectPlaceUsers(
            @PathVariable Long groupId, @RequestParam Long bestPlaceId
    ) {
        return CustomResponseEntity.success(
                voteService.readSelectPlaceUsers(groupId, bestPlaceId)
        );
    }

    // 투표 종료하기 API
    @PatchMapping("/{groupId}/vote")
    public CustomResponseEntity<VoteSelectResultResponse> conclusionVote(
            @PathVariable Long groupId, @Login Users user
    ) {
        return CustomResponseEntity.success(voteService.conclusionVote(groupId, user));
    }

    // 재투표 API
    @PutMapping("/{spaceId}/vote")
    public CustomResponseEntity<VoteCreateResponse> reCreateVote(
            @PathVariable Long spaceId,
            @RequestBody @Valid VoteCreateRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(voteService.reCreateVote(request, spaceId, user));
    }
}
