package com.moim.backend.domain.groupvote.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
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

    private LocalDateTime endAt;
}
