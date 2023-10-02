package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.Groups;
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
public class GroupDetailResponse {
    private Long groupId;
    private String adminEmail;
    private String name;
    private String date;
    private List<GroupRegionResponse> participantsByRegion;

    public static GroupDetailResponse response(Groups group, Users admin, List<GroupRegionResponse> participantsByRegion) {
        String date = group.getDate()
                .map(d -> d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .orElse("none");

        return GroupDetailResponse.builder()
                .groupId(group.getGroupId())
                .name(group.getName())
                .adminEmail(admin.getEmail())
                .date(date)
                .participantsByRegion(participantsByRegion)
                .build();
    }
}
