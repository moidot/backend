package com.moim.backend.domain.groupvote.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;

    @NotNull
    private Long groupId;

    @NotNull
    private Boolean isClosed;

    @NotNull
    private Boolean isAnonymous;

    @NotNull
    private Boolean isEnabledMultipleChoice;

    @NotNull
    private LocalDateTime endAt;
}
