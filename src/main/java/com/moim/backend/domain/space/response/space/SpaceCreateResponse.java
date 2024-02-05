package com.moim.backend.domain.space.response.space;

import com.moim.backend.domain.space.entity.Space;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@Builder
public class SpaceCreateResponse {
    private Long groupId;
    private Long adminId;
    private String name;
    private String date;
    private String fixedPlace;

    public static SpaceCreateResponse response(Space group) {
        return SpaceCreateResponse.builder()
                .groupId(group.getSpaceId())
                .adminId(group.getAdminId())
                .name(group.getName())
                .date(group.getDate().map(date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .orElse("none"))
                .fixedPlace(group.getPlace())
                .build();
    }
}
