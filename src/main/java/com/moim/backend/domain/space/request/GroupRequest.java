package com.moim.backend.domain.space.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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
        @NotNull(message = "스페이스 아이디를 입력하지 않았습니다.")
        private Long groupId;

        @NotBlank(message = "닉네임을 입력하지 않았습니다.")
        private String userName;

        @NotBlank(message = "출발 위치가 입력하지 않았습니다.")
        private String locationName;

        @NotNull(message = "위도를 입력하지 않았습니다.")
        private Double latitude;

        @NotNull(message = "경도를 입력하지 않았습니다.")
        private Double longitude;

        @NotNull(message = "이동 수단을 입력하지 않았습니다.")
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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipateUpdate {

        @NotNull
        private Long participateId;

        @NotBlank(message = "닉네임을 입력하지 않았습니다.")
        private String userName;

        @NotBlank(message = "출발 위치가 입력되지 않았습니다.")
        private String locationName;

        @NotNull
        private Double latitude;

        @NotNull
        private Double longitude;

        @NotNull
        private String transportation;

        public GroupServiceRequest.ParticipateUpdate toServiceRequest() {
            return GroupServiceRequest.ParticipateUpdate.builder()
                    .participateId(participateId)
                    .userName(userName)
                    .locationName(locationName)
                    .latitude(latitude)
                    .longitude(longitude)
                    .transportation(transportation)
                    .build();
        }
    }
}
