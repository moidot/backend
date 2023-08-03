package com.moim.backend.domain.groupvote.response;

import com.moim.backend.domain.groupvote.entity.Vote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class VoteResponse {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Create {
        private Long voteId;
        private Long groupId;
        private Boolean isClosed;
        private Boolean isAnonymous;
        private Boolean isEnabledMultipleChoice;
        private String endAt;

        public static VoteResponse.Create response(Vote vote) {
            Optional<LocalDateTime> endAt = Optional.ofNullable(vote.getEndAt());
            return Create.builder()
                    .voteId(vote.getVoteId())
                    .groupId(vote.getGroupId())
                    .isClosed(vote.getIsClosed())
                    .isAnonymous(vote.getIsAnonymous())
                    .isEnabledMultipleChoice(vote.getIsEnabledMultipleChoice())
                    .endAt(endAt
                            .map(time -> time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                            .orElse("none"))
                    .build();
        }
    }
}
