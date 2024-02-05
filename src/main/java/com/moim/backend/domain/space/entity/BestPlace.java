package com.moim.backend.domain.space.entity;

import com.moim.backend.domain.spacevote.entity.SelectPlace;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

import static lombok.AccessLevel.*;

@Entity
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
public class BestPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bestPlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @NotNull
    private String placeName;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @OneToMany(mappedBy = "bestPlace", orphanRemoval = true)
    private List<SelectPlace> selectPlaces;

}
