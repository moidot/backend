package com.moim.backend.domain.groupvote.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import static java.lang.Boolean.TRUE;

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

    @Version
    private Long version;

    public void conclusionVote() {
        this.isClosed = TRUE;
    }
}
