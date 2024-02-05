package com.moim.backend.domain.spacevote.response;

import com.moim.backend.domain.spacevote.entity.Vote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class VoteCreateResponse {
    private Long voteId;
    private Long groupId;
    private Boolean isClosed;
    private Boolean isAnonymous;
    private Boolean isEnabledMultipleChoice;
    private String endAt;

    public static VoteCreateResponse response(Vote vote) {
        String endAt = Optional.ofNullable(vote.getEndAt())
                .map(time -> time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                .orElse("none");

        return VoteCreateResponse.builder()
                .voteId(vote.getVoteId())
                .groupId(vote.getSpaceId())
                .isClosed(vote.getIsClosed())
                .isAnonymous(vote.getIsAnonymous())
                .isEnabledMultipleChoice(vote.getIsEnabledMultipleChoice())
                .endAt(endAt)
                .build();
    }
}
