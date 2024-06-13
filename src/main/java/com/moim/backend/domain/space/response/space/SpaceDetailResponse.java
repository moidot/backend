package com.moim.backend.domain.space.response.space;

import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SpaceDetailResponse {
    private Long groupId;
    private String adminEmail;
    private String name;
    private String date;
    private List<SpaceRegionResponse> participantsByRegion;

    public static SpaceDetailResponse response(Space group, Users admin, List<SpaceRegionResponse> participantsByRegion) {
        String date = group.getDate()
                .map(d -> d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .orElse("none");

        return SpaceDetailResponse.builder()
                .groupId(group.getSpaceId())
                .name(group.getName())
                .adminEmail(admin.getEmail())
                .date(date)
                .participantsByRegion(participantsByRegion)
                .build();
    }
}
