package com.moim.backend.domain.groupvote.controller;

import com.moim.backend.domain.groupvote.request.VoteRequest;
import com.moim.backend.domain.groupvote.response.VoteResponse;
import com.moim.backend.domain.groupvote.service.VoteService;
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
    public CustomResponseEntity<VoteResponse.Create> createVote(
            @PathVariable Long groupId,
            @RequestBody @Valid VoteRequest.Create request, @Login Users user
    ) {
        return CustomResponseEntity.success(voteService.createVote(request.toServiceRequest(), groupId, user));
    }

    // 투표 읽기 API
    @GetMapping("/{groupId}/vote")
    public CustomResponseEntity<VoteResponse.SelectResult> readVote(
            @PathVariable Long groupId, @Login Users user
    ) {
        return CustomResponseEntity.success(voteService.readVote(groupId, user));
    }

    // 투표 참여 API
    @PostMapping("/{groupId}/vote/select")
    public CustomResponseEntity<VoteResponse.SelectResult> selectVote(
            @PathVariable Long groupId, @RequestParam List<Long> bestPlaceIds, @Login Users user
    ) {
        return CustomResponseEntity.success(
                voteService.selectVote(groupId, bestPlaceIds, user, LocalDateTime.now())
        );
    }

    // 해당 장소 투표한 인원 리스트 조회하기 API
    @GetMapping("/{groupId}/vote/select")
    public CustomResponseEntity<List<VoteResponse.SelectPlaceUser>> readSelectPlaceUsers(
            @PathVariable Long groupId, @RequestParam Long bestPlaceId, @Login Users user
    ) {
        return CustomResponseEntity.success(
                voteService.readSelectPlaceUsers(groupId, bestPlaceId, user)
        );
    }

    // 투표 종료하기 API
    @PatchMapping("/{groupId}/vote")
    public CustomResponseEntity<VoteResponse.SelectResult> conclusionVote(
            @PathVariable Long groupId, @Login Users user
    ) {
        return CustomResponseEntity.success(voteService.conclusionVote(groupId, user));
    }

}
