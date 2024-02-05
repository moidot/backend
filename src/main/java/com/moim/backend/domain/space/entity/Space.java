package com.moim.backend.domain.space.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spaceId;

    @NotNull
    private Long adminId;

    @NotNull
    private String name;

    private LocalDate date;

    private String place;

    @OneToMany(mappedBy = "space", orphanRemoval = true)
    private List<Participation> participations;

    @OneToMany(mappedBy = "space", orphanRemoval = true)
    private List<BestPlace> bestPlaces;

    public void confirmPlace(String place) {
        this.place = place;
    }

    public void updateGroupName(String groupName) {
        this.name = groupName;
    }

    public Optional<LocalDate> getDate() {
        return Optional.ofNullable(date);
    }
}
