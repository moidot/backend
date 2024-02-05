package com.moim.backend.domain.space.response.space;

import com.moim.backend.domain.space.entity.Space;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@Builder
public class SpaceMyParticipateResponse {
    private Long groupId;
    private String groupName;
    private String groupAdminName;
    private String groupDate;
    private Integer groupParticipates;
    private String confirmPlace;
    private Boolean isAdmin;
    private List<String> bestPlaceNames;
    private List<String> participantNames;

    public static SpaceMyParticipateResponse response(
            Space group, String groupAdminName, boolean isAdmin, List<String> bestPlaceNames, List<String> participantNames
    ) {
        return SpaceMyParticipateResponse.builder()
                .groupId(group.getSpaceId())
                .groupName(group.getName())
                .groupAdminName(groupAdminName)
                .groupDate(group.getDate()
                        .map(date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .orElse("none"))
                .groupParticipates(group.getParticipations().size())
                .confirmPlace(group.getPlace())
                .isAdmin(isAdmin)
                .bestPlaceNames(bestPlaceNames)
                .participantNames(participantNames)
                .build();
    }
}
