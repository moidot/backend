package com.moim.backend.domain.space.response.space;

import com.moim.backend.domain.space.entity.Participation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@Builder
public class SpaceParticipateUpdateResponse {
    private String locationName;
    private String transportation;

    public static SpaceParticipateUpdateResponse response(Participation participation) {
        return SpaceParticipateUpdateResponse.builder()
                .locationName(participation.getLocationName())
                .transportation(participation.getTransportation().name())
                .build();
    }
}
