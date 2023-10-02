package com.moim.backend.domain.space.response.group;

import lombok.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor
@Getter
@Builder
public class GroupRegionResponse {
    private String regionName;

    @Setter
    private List<GroupParticipationsResponse> participations;

    public static GroupRegionResponse toLocalEntity(String region, GroupParticipationsResponse participation) {
        return GroupRegionResponse.builder()
                .regionName(region)
                .participations(List.of(participation))
                .build();
    }

    public static GroupRegionResponse toResponse(String regionName, List<GroupParticipationsResponse> participations) {
        return new GroupRegionResponse(regionName, participations);
    }
}
