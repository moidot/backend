package com.moim.backend.domain.space.request.controller;

import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.request.service.GroupCreateServiceRequest;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.*;

@Getter
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor
public class GroupCreateRequest {
    @NotBlank(message = "그룹 이름을 입력하지 않았습니다.")
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotBlank(message = "닉네임을 입력하지 않았습니다.")
    private String userName;

    @NotBlank(message = "출발 위치가 입력하지 않았습니다.")
    private String locationName;

    @NotNull(message = "위도를 입력하지 않았습니다.")
    private Double latitude;

    @NotNull(message = "경도를 입력하지 않았습니다.")
    private Double longitude;

    @Enumerated(STRING)
    @NotNull(message = "이동 수단을 입력하지 않았습니다.")
    private TransportationType transportationType;

    private String password;

    public GroupCreateServiceRequest toServiceRequest() {
        return GroupCreateServiceRequest.builder()
                .name(name)
                .date(date)
                .userName(userName)
                .locationName(locationName)
                .latitude(latitude)
                .longitude(longitude)
                .transportationType(transportationType)
                .password(password)
                .build();
    }

    public static GroupCreateRequest toRequest(String name, LocalDate date, String userName, String locationName, Double latitude, Double longitude, TransportationType transportationType, String password) {
        return new GroupCreateRequest(
                name, date, userName,
                locationName, latitude, longitude,
                transportationType, password
        );
    }
}
