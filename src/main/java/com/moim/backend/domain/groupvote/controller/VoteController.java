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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
public class VoteController {
    private final VoteService voteService;

    @PostMapping("/{groupId}/vote")
    public CustomResponseEntity<VoteResponse.Create> createVote(
            @PathVariable Long groupId,
            @RequestBody @Valid VoteRequest.Create request, @Login Users user
    ) {
        return CustomResponseEntity.success(voteService.createVote(request.toServiceRequest(), groupId, user));
    }
}
