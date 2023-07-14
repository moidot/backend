package com.moim.backend.domain.space.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private LocalDateTime date;

    private String place;

    @Builder
    private Groups(Long spaceId, Long adminId, String name, LocalDateTime date, String place) {
        this.groupId = spaceId;
        this.adminId = adminId;
        this.name = name;
        this.date = date;
        this.place = place;
    }
}
