package com.moim.backend.domain.space.request.controller;

import com.moim.backend.domain.space.request.service.SpaceNameUpdateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SpaceNameUpdateRequest {
    @NotBlank(message = "수정할 그룹 이름을 입력하지 않았습니다.")
    private String groupName;

    public SpaceNameUpdateServiceRequest toServiceRequest() {
        return SpaceNameUpdateServiceRequest.builder()
                .groupName(groupName)
                .build();
    }
}
