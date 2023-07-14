package com.moim.backend.domain.groupvote.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SelectPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long selectPlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_place_id")
    private VotePlace votePlaceId;

    @NotNull
    private String userName;

}
