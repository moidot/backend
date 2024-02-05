package com.moim.backend.domain.space.response.space;

import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.Space;
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
    private Boolean isAdmin;

    public static SpaceParticipationsResponse toParticipateEntity(Space space, Participation participation, Users user) {
        return SpaceParticipationsResponse.builder()
                .participationId(participation.getParticipationId())
                .userEmail(user.getEmail())
                .userName(participation.getUserName())
                .locationName(participation.getLocationName())
                .transportation(participation.getTransportation().name())
                .isAdmin(space.getAdminId() == participation.getUserId())
                .build();
    }

    public static SpaceParticipationsResponse toResponse(Long participationId, String userEmail, String userName, String locationName, String transportation, Boolean isAdmin) {
        return new SpaceParticipationsResponse(
                participationId, userEmail, userName, locationName, transportation, isAdmin
        );
    }
}
