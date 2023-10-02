package com.moim.backend.domain.space.response.group;

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
public class GroupParticipationsResponse {
    private Long participationId;
    private String userEmail;
    private String userName;
    private String locationName;
    private String transportation;

    public static GroupParticipationsResponse toParticipateEntity(Participation participation, Users user) {
        return GroupParticipationsResponse.builder()
                .participationId(participation.getParticipationId())
                .userEmail(user.getEmail())
                .userName(participation.getUserName())
                .locationName(participation.getLocationName())
                .transportation(participation.getTransportation().name())
                .build();
    }

    public static GroupParticipationsResponse toResponse(Long participationId, String userEmail, String userName, String locationName, String transportation) {
        return new GroupParticipationsResponse(
                participationId, userEmail, userName,
                locationName, transportation
        );
    }
}
