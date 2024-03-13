package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.config.OdsayProperties;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.response.BusGraphicDataResponse;
import com.moim.backend.domain.space.response.BusPathResponse;
import com.moim.backend.domain.space.response.CarMoveInfo;
import com.moim.backend.domain.space.response.PlaceRouteResponse;
import com.moim.backend.domain.user.config.KakaoProperties;
import com.moim.backend.global.aspect.TimeCheck;
import com.moim.backend.global.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectionService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final OdsayProperties odsayProperties;
    private final KakaoProperties kakaoProperties;

    @TimeCheck
    public Optional<PlaceRouteResponse.MoveUserInfo> getBusRouteToResponse(
            BestPlace bestPlace, Space group, Participation participation
    ) {
        Optional<PlaceRouteResponse.MoveUserInfo> moveUserInfo = Optional.empty();
        URI searchPathUri = odsayProperties.getSearchPathUriWithParams(bestPlace, participation);
        BusPathResponse busPathResponse = restTemplate.getForObject(searchPathUri, BusPathResponse.class);

        if (busPathResponse.getResult() == null) {
            LoggingUtil.builder()
                    .title("버스 길찾기")
                    .status("실패")
                    .message("지역: " + bestPlace.getPlaceName() + ", url: " + searchPathUri)
                    .build()
                    .print();
        } else {
            URI graphicDataUri = odsayProperties.getGraphicDataUriWIthParams(busPathResponse.getPathInfoMapObj());
            BusGraphicDataResponse busGraphicDataResponse = restTemplate.getForObject(
                    graphicDataUri, BusGraphicDataResponse.class
            );
            if (busPathResponse.getResult() == null) {
                LoggingUtil.builder()
                        .title("버스 그래픽 데이터 조회")
                        .status("실패")
                        .message("지역: " + bestPlace.getPlaceName() + ", url: " + graphicDataUri)
                        .build()
                        .print();
            } else {
                moveUserInfo = Optional.of(new PlaceRouteResponse.MoveUserInfo(
                        group, participation, busGraphicDataResponse, busPathResponse, bestPlace
                ));
            }
        }
        return moveUserInfo;
    }

    @TimeCheck
    public Optional<PlaceRouteResponse.MoveUserInfo> getCarRouteToResponse(
            BestPlace bestPlace, Space group, Participation participation
    ) {
        Optional<PlaceRouteResponse.MoveUserInfo> moveUserInfo = Optional.empty();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoProperties.getClientId());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        URI searchCarPathUri = kakaoProperties.getSearchCarPathUriWithParams(bestPlace, participation);
        CarMoveInfo carMoveInfo = restTemplate.exchange(
                searchCarPathUri, HttpMethod.GET, new HttpEntity<>(headers), CarMoveInfo.class
        ).getBody();
        if (carMoveInfo.getRoutes() == null) {
            LoggingUtil.builder()
                    .title("차 길찾기")
                    .status("실패")
                    .message("지역: " + bestPlace.getPlaceName() + ", url: " + searchCarPathUri)
                    .build()
                    .print();
        } else {
            moveUserInfo = Optional.of(new PlaceRouteResponse.MoveUserInfo(group, participation, carMoveInfo, bestPlace));
        }
        return moveUserInfo;
    }

}
