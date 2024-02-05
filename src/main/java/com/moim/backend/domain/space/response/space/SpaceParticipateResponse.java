package com.moim.backend.domain.space.response.space;

import com.moim.backend.domain.space.entity.Participation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpaceParticipateResponse {
    private Long participationId;
    private Long groupId;
    private Long userId;
    private String userName;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String transportation;

    public static SpaceParticipateResponse response(Participation participation) {
        return SpaceParticipateResponse.builder()
                .participationId(participation.getParticipationId())
                .groupId(participation.getSpace().getSpaceId())
                .userId(participation.getUserId())
                .userName(participation.getUserName())
                .locationName(participation.getLocationName())
                .latitude(participation.getLatitude())
                .longitude(participation.getLongitude())
                .transportation(participation.getTransportation().name())
                .build();
    }
}
