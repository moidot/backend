package com.moim.backend.global.dto;

import com.moim.backend.domain.hotplace.entity.HotPlace;
import com.moim.backend.domain.subway.entity.Subway;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BestRegion {

    private String name;
    private double latitude;
    private double longitude;

    public BestRegion(Subway subway) {
        this.name = subway.getName();
        this.latitude = subway.getLatitude().doubleValue();
        this.longitude = subway.getLongitude().doubleValue();
    }

    public BestRegion(HotPlace hotPlace) {
        this.name = hotPlace.getName();
        this.latitude = hotPlace.getLatitude().doubleValue();
        this.longitude = hotPlace.getLongitude().doubleValue();
    }

}
