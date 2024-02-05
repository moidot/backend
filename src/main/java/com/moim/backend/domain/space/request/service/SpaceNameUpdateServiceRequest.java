package com.moim.backend.domain.space.request.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceNameUpdateServiceRequest {
    private String groupName;
}
