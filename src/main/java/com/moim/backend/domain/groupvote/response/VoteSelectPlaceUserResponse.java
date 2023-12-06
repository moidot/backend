package com.moim.backend.domain.groupvote.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class VoteSelectPlaceUserResponse {
    private int totalVoteNum;
    private List<VoteParticipation> voteParticipations;

    public static VoteSelectPlaceUserResponse response(int totalVoteNum, List<VoteParticipation> voteParticipations) {
        return VoteSelectPlaceUserResponse.builder()
                .totalVoteNum(totalVoteNum)
                .voteParticipations(voteParticipations)
                .build();
    }
}
