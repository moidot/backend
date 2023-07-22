package com.moim.backend.domain.space.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BestPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bestPlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Groups group;

    @NotNull
    private String placeName;

    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;

    @Builder
    private BestPlace(Long bestPlaceId, Groups group, String placeName, Double latitude, Double longitude) {
        this.bestPlaceId = bestPlaceId;
        this.group = group;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
