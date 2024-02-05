package com.moim.backend.domain.space.response.space;

import lombok.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor
@Getter
@Builder
public class SpaceRegionResponse {
    private String regionName;

    @Setter
    private List<SpaceParticipationsResponse> participations;

    public static SpaceRegionResponse toLocalEntity(String region, SpaceParticipationsResponse participation) {
        return SpaceRegionResponse.builder()
                .regionName(region)
                .participations(List.of(participation))
                .build();
    }

    public static SpaceRegionResponse toResponse(String regionName, List<SpaceParticipationsResponse> participations) {
        return new SpaceRegionResponse(regionName, participations);
    }
}
