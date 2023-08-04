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
@RequestMapping("/api/v1/group")
public class VoteController {
    private final VoteService voteService;

    // 투표 생성(시작)
    @PostMapping("/{groupId}/vote")
    public CustomResponseEntity<VoteResponse.Create> createVote(
            @PathVariable Long groupId,
            @RequestBody @Valid VoteRequest.Create request, @Login Users user
    ) {
        return CustomResponseEntity.success(voteService.createVote(request.toServiceRequest(), groupId, user));
    }

    // 투표 하기
    @PostMapping("/{groupId}/vote/select")
    public CustomResponseEntity<VoteResponse.SelectResult> selectVote(
            @PathVariable Long groupId, @RequestParam List<Long> bestPlaceIds, @Login Users user
    ) {
        return CustomResponseEntity.success(
                voteService.selectVote(groupId, bestPlaceIds, user, LocalDateTime.now())
        );
    }

}
