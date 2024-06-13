package com.moim.backend.domain.space.request;

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
}
