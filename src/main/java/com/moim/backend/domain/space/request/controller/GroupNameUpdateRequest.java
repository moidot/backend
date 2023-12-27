package com.moim.backend.domain.space.request.controller;

import com.moim.backend.domain.space.request.service.GroupNameUpdateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupNameUpdateRequest {
    @NotBlank(message = "수정할 그룹 이름을 입력하지 않았습니다.")
    private String groupName;

    public GroupNameUpdateServiceRequest toServiceRequest() {
        return GroupNameUpdateServiceRequest.builder()
                .groupName(groupName)
                .build();
    }
}
