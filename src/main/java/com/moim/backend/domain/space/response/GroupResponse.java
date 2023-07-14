package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.Groups;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

public class GroupResponse {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private Long groupId;
        private Long adminId;
        private String name;
        private String date;
        private String fixedPlace;

        @Builder
        private Create(Long groupId, Long adminId, String name, String date, String fixedPlace) {
            this.groupId = groupId;
            this.adminId = adminId;
            this.name = name;
            this.date = date;
            this.fixedPlace = fixedPlace;
        }

        public static GroupResponse.Create response(Groups group) {
            return Create.builder()
                    .groupId(group.getGroupId())
                    .adminId(group.getAdminId())
                    .name(group.getName())
                    .date(group.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .fixedPlace(group.getPlace())
                    .build();
        }
    }
}
