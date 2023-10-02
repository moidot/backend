package com.moim.backend.domain.groupvote.response;

import com.moim.backend.domain.space.entity.Participation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class VoteSelectPlaceUserResponse {
    private Long participationId;
    private Long userId;
    private String nickName;
    private Boolean isAdmin;

    public static VoteSelectPlaceUserResponse response(Participation participation, Boolean isAdmin) {
        return VoteSelectPlaceUserResponse.builder()
                .participationId(participation.getParticipationId())
                .userId(participation.getUserId())
                .nickName(participation.getUserName())
                .isAdmin(isAdmin)
                .build();
    }
}
