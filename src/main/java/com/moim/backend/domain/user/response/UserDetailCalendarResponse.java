package com.moim.backend.domain.user.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.SpaceCalendar;
import com.moim.backend.domain.user.entity.UserCalendar;
import com.moim.backend.global.util.DateParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserDetailCalendarResponse {
    private String spaceName;
    private String scheduleName;
    private String date;
    private String locationName;
    private String color;

    public static UserDetailCalendarResponse spaceCalendarResponse(Space space, SpaceCalendar spaceCalendar) {
        return UserDetailCalendarResponse.builder()
                .spaceName(space.getName())
                .scheduleName(spaceCalendar.getScheduleName())
                .date(DateParser.timeFormat(spaceCalendar.getDate()))
                .locationName(spaceCalendar.getLocationName())
                .color("space")
                .build();
    }

    public static UserDetailCalendarResponse userCalendarResponse(UserCalendar userCalendar) {
        return UserDetailCalendarResponse.builder()
                .spaceName("")
                .scheduleName(userCalendar.getScheduleName())
                .date(DateParser.timeFormat(userCalendar.getDate()))
                .locationName(userCalendar.getLocationName())
                .color(userCalendar.getColor())
                .build();
    }
}
