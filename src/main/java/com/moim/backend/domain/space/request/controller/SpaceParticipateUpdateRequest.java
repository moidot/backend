package com.moim.backend.domain.space.request.controller;

import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.request.service.SpaceParticipateUpdateServiceRequest;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpaceParticipateUpdateRequest {
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

    @Enumerated(value = STRING)
    @NotNull
    private TransportationType transportationType;

    public SpaceParticipateUpdateServiceRequest toServiceRequest() {
        return SpaceParticipateUpdateServiceRequest.builder()
                .participateId(participateId)
                .userName(userName)
                .locationName(locationName)
                .latitude(latitude)
                .longitude(longitude)
                .transportationType(transportationType)
                .build();
    }

    public static SpaceParticipateUpdateRequest toRequest(Long participateId, String userName, String locationName, Double latitude, Double longitude, TransportationType transportationType) {
        return new SpaceParticipateUpdateRequest(
                participateId, userName, locationName,
                latitude, longitude, transportationType
        );
    }
}
