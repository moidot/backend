package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.config.OdsayProperties;
import com.moim.backend.domain.space.config.TmapProperties;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.response.BusGraphicDataResponse;
import com.moim.backend.domain.space.response.BusPathResponse;
import com.moim.backend.domain.space.response.CarMoveInfo;
import com.moim.backend.domain.space.response.PathDto;
import com.moim.backend.domain.space.response.PlaceRouteResponse;
import com.moim.backend.domain.space.response.TmapPublicPathResponse;
import com.moim.backend.domain.user.config.KakaoProperties;
import com.moim.backend.global.aspect.TimeCheck;
import com.moim.backend.global.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectionService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final OdsayProperties odsayProperties;
    private final KakaoProperties kakaoProperties;
    private final TmapProperties tmapProperties;
    private final TmapService tmapService;

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

    @TimeCheck
    public Optional<PlaceRouteResponse.MoveUserInfo> getBusRouteToResponseWithTmap(
            BestPlace bestPlace, Space group, Participation participation
    ) {
        Optional<PlaceRouteResponse.MoveUserInfo> moveUserInfo = Optional.empty();

        ResponseEntity<TmapPublicPathResponse> response = restTemplate.postForEntity(
                tmapProperties.getSearchPathUri(),
                createHttpEntity(bestPlace, participation),
                TmapPublicPathResponse.class
        );

        TmapPublicPathResponse tmapPublicPathResponse = response.getBody();

        if (tmapPublicPathResponse == null || tmapPublicPathResponse.getMetaData() == null) {
            LoggingUtil.builder()
                    .title("TMAP 대중교통 길찾기")
                    .status("실패")
                    .message("bestplace: " + bestPlace + ", participation: " + participation)
                    .build()
                    .print();
        } else {
            List<PathDto> path = tmapService.getPath(tmapPublicPathResponse);

            moveUserInfo = Optional.of(new PlaceRouteResponse.MoveUserInfo(group, participation, tmapPublicPathResponse, bestPlace, path));
        }

        return moveUserInfo;
    }

    private HttpEntity<?> createHttpEntity(BestPlace bestPlace, Participation participation) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("appKey", tmapProperties.getAppKey());

        Map<String, String> requestBody = tmapProperties.getRequestParameter(
                participation.getLongitude(),
                participation.getLatitude(),
                bestPlace.getLongitude(),
                bestPlace.getLatitude()
        );

        return new HttpEntity<>(requestBody, headers);
    }
}
