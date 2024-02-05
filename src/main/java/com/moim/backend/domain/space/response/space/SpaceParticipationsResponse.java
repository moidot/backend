package com.moim.backend.domain.space.response.space;

import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpaceParticipationsResponse {
    private Long participationId;
    private String userEmail;
    private String userName;
    private String locationName;
    private String transportation;

    public static SpaceParticipationsResponse toParticipateEntity(Participation participation, Users user) {
        return SpaceParticipationsResponse.builder()
                .participationId(participation.getParticipationId())
                .userEmail(user.getEmail())
                .userName(participation.getUserName())
                .locationName(participation.getLocationName())
                .transportation(participation.getTransportation().name())
                .build();
    }

    public static SpaceParticipationsResponse toResponse(Long participationId, String userEmail, String userName, String locationName, String transportation) {
        return new SpaceParticipationsResponse(
                participationId, userEmail, userName,
                locationName, transportation
        );
    }
}
