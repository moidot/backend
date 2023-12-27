package com.moim.backend.domain.space.response.group;

import com.moim.backend.domain.space.entity.Groups;
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
public class GroupMyParticipateResponse {
    private Long groupId;
    private String groupName;
    private String groupAdminName;
    private String groupDate;
    private Integer groupParticipates;
    private String confirmPlace;
    private List<String> bestPlaceNames;
    private List<String> participantNames;

    public static GroupMyParticipateResponse response(
            Groups group, String groupAdminName, List<String> bestPlaceNames, List<String> participantNames
    ) {
        return GroupMyParticipateResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getName())
                .groupAdminName(groupAdminName)
                .groupDate(group.getDate()
                        .map(date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .orElse("none"))
                .groupParticipates(group.getParticipations().size())
                .confirmPlace(group.getPlace())
                .bestPlaceNames(bestPlaceNames)
                .participantNames(participantNames)
                .build();
    }
}
