package com.moim.backend.domain.space.request.service;

import com.moim.backend.domain.space.entity.TransportationType;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
public class GroupParticipateServiceRequest {
    private Long groupId;
    private String userName;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private TransportationType transportationType;
    private String password;
}
