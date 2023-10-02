package com.moim.backend.domain.groupvote.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class VoteResponse {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class SelectPlaceUser {
        private Long participationId;
        private Long userId;
        private String nickName;
        private Boolean isAdmin;

        public static VoteResponse.SelectPlaceUser response(Participation participation, Boolean isAdmin) {
            return SelectPlaceUser.builder()
                    .participationId(participation.getParticipationId())
                    .userId(participation.getUserId())
                    .nickName(participation.getUserName())
                    .isAdmin(isAdmin)
                    .build();
        }
    }
}
