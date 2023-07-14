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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Groups group;

    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;

    @Builder
    private BestPlace(Long id, Groups group, Double latitude, Double longitude) {
        this.id = id;
        this.group = group;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
