package com.moim.backend.domain.space.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class GroupRequest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {
        @NotBlank(message = "그룹 이름을 입력하지 않았습니다.")
        private String name;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;

        public GroupServiceRequest.Create toServiceRequest() {
            return GroupServiceRequest.Create.builder()
                    .name(name)
                    .date(date)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Participate {
        @NotNull
        private Long groupId;

        @NotBlank(message = "별명을 입력하지 않았습니다.")
        private String userName;

        @NotBlank(message = "출발 위치가 입력되지 않았습니다.")
        private String locationName;

        @NotNull
        private Double latitude;

        @NotNull
        private Double longitude;

        @NotNull
        private String transportation;

        private String password;

        public GroupServiceRequest.Participate toServiceRequest() {
            return GroupServiceRequest.Participate.builder()
                    .groupId(groupId)
                    .userName(userName)
                    .locationName(locationName)
                    .latitude(latitude)
                    .longitude(longitude)
                    .transportation(transportation)
                    .password(password)
                    .build();
        }
    }
}
