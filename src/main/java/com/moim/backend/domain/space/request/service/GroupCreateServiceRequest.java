package com.moim.backend.domain.space.request.service;

import com.moim.backend.domain.space.entity.TransportationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupCreateServiceRequest {
    private String name;
    private LocalDate date;
    private String userName;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private TransportationType transportationType;
    private String password;
}
