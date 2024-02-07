package com.moim.backend.domain.space.entity;

import com.moim.backend.domain.space.request.service.SpaceParticipateUpdateServiceRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @NotNull
    private Long userId;

    @NotNull
    private String userName;

    @NotNull
    private String locationName;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @Enumerated(value = EnumType.STRING)
    private TransportationType transportation;

    private String password;

    @Builder
    private Participation(Long participationId, Space space, Long userId, String userName, String locationName, Double latitude, Double longitude, TransportationType transportation, String password) {
        this.participationId = participationId;
        this.space = space;
        this.userId = userId;
        this.userName = userName;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.transportation = transportation;
        this.password = password;
    }

    public void update(SpaceParticipateUpdateServiceRequest request) {
        this.userName = request.getUserName();
        this.locationName = request.getLocationName();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.transportation = request.getTransportationType();
    }
}
