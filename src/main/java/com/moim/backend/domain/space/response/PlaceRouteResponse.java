package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRouteResponse {

    private String name;
    private Double latitude;
    private Double longitude;
    private List<MoveUserInfo> moveUserInfo = new ArrayList<>();

    public PlaceRouteResponse(
            BestPlace bestPlace,
            List<MoveUserInfo> moveUserInfoList
    ) {
        this.name = bestPlace.getPlaceName();
        this.latitude = bestPlace.getLatitude();
        this.longitude = bestPlace.getLongitude();
        this.moveUserInfo = moveUserInfoList;
    }
}
