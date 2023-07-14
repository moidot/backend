package com.moim.backend.domain.groupvote.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VotePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long votePlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote voteId;

    @NotNull
    private String place;

}
