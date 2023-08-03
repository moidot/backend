package com.moim.backend.domain.groupvote.service;

import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.groupvote.repository.VoteRepository;
import com.moim.backend.domain.groupvote.request.VoteServiceRequest;
import com.moim.backend.domain.groupvote.response.VoteResponse;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.repository.GroupRepository;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.common.Result;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final GroupRepository groupRepository;
    private final VoteRepository voteRepository;

    public VoteResponse.Create createVote(VoteServiceRequest.Create request, Long groupId, Users user) {
        Groups group = groupRepository.findById(groupId)
                .orElseThrow(
                        () -> new CustomException(Result.NOT_FOUND_GROUP)
                );

        if (!group.getAdminId().equals(user.getUserId())) {
            throw new CustomException(Result.NOT_ADMIN_USER);
        }

        Vote vote = voteRepository.save(
                Vote.builder()
                        .groupId(groupId)
                        .isAnonymous(request.getIsAnonymous())
                        .isEnabledMultipleChoice(request.getIsEnabledMultipleChoice())
                        .isClosed(false)
                        .endAt(request.getEndAt().orElse(null))
                        .build()
        );

        return VoteResponse.Create.response(vote);
    }
}
