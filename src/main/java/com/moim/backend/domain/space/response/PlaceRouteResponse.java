package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자별 이동 정보 상세")
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
