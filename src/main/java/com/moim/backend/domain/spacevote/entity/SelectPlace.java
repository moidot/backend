package com.moim.backend.domain.spacevote.entity;

import com.moim.backend.domain.space.entity.BestPlace;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
public class SelectPlace {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long selectPlaceId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "best_place_id")
    private BestPlace bestPlace;

    @NotNull
    private Long userId;

}
