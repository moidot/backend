package com.moim.backend.domain.space.request;

import com.moim.backend.domain.space.entity.TransportationType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor
@Builder
public class SpaceParticipateRequest {
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

    @Enumerated(STRING)
    @NotNull(message = "이동 수단을 입력하지 않았습니다.")
    private TransportationType transportationType;

    private String password;

    public static SpaceParticipateRequest toRequest(Long groupId, String userName, String locationName, Double latitude, Double longitude, TransportationType transportationType, String password) {
        return new SpaceParticipateRequest(
                groupId, userName, locationName,
                latitude, longitude, transportationType,
                password
        );
    }
}
