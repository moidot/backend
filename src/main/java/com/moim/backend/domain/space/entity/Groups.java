package com.moim.backend.domain.space.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Groups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @NotNull
    private Long adminId;

    @NotNull
    private String name;

    private LocalDate date;

    private String place;

    @OneToMany(mappedBy = "group", orphanRemoval = true)
    private List<Participation> participations;

    @Builder
    private Groups(Long groupId, Long adminId, String name, LocalDate date, String place, List<Participation> participations) {
        this.groupId = groupId;
        this.adminId = adminId;
        this.name = name;
        this.date = date;
        this.place = (place == null) ? "none" : place;
        this.participations = participations;
    }
}
