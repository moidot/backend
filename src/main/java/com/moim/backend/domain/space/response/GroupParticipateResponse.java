package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.Participation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupParticipateResponse {
    private Long participationId;
    private Long groupId;
    private Long userId;
    private String userName;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String transportation;

    public static GroupParticipateResponse response(Participation participation) {
        return GroupParticipateResponse.builder()
                .participationId(participation.getParticipationId())
                .groupId(participation.getGroup().getGroupId())
                .userId(participation.getUserId())
                .userName(participation.getUserName())
                .locationName(participation.getLocationName())
                .latitude(participation.getLatitude())
                .longitude(participation.getLongitude())
                .transportation(participation.getTransportation().name())
                .build();
    }
}
