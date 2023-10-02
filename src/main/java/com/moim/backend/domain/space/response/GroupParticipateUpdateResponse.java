package com.moim.backend.domain.space.response;

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
public class GroupParticipateUpdateResponse {
    private String locationName;
    private String transportation;

    public static GroupParticipateUpdateResponse response(Participation participation) {
        return GroupParticipateUpdateResponse.builder()
                .locationName(participation.getLocationName())
                .transportation(participation.getTransportation().name())
                .build();
    }
}
