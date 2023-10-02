package com.moim.backend.domain.space.response;

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
    private List<GroupResponse.Participations> participations;

    public static GroupRegionResponse toLocalEntity(String region, GroupResponse.Participations participation) {
        return GroupRegionResponse.builder()
                .regionName(region)
                .participations(List.of(participation))
                .build();
    }

    public static GroupRegionResponse toResponse(String regionName, List<GroupResponse.Participations> participations) {
        return new GroupRegionResponse(regionName, participations);
    }
}
