package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.config.OdsayProperties;
import com.moim.backend.domain.space.config.TmapProperties;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.response.CarPathResponse;
import com.moim.backend.domain.space.response.MoveUserInfo;
import com.moim.backend.domain.space.response.PathDto;
import com.moim.backend.domain.space.response.TmapPublicPathResponse;
import com.moim.backend.domain.space.response.TmapWalkPathResponse;
import com.moim.backend.domain.user.config.KakaoProperties;
import com.moim.backend.global.aspect.TimeCheck;
import com.moim.backend.global.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
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
    public Optional<MoveUserInfo> getCarRoute(
            BestPlace bestPlace, Space group, Participation participation
    ) {
        log.info("[getCarRoute {} START]", participation.getUserName());
        Optional<MoveUserInfo> moveUserInfo = Optional.empty();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoProperties.getClientId());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        URI searchCarPathUri = kakaoProperties.getSearchCarPathUriWithParams(bestPlace, participation);
        log.info("searchCarPathUri: {}", searchCarPathUri);
        CarPathResponse carPathResponse = restTemplate.exchange(
                searchCarPathUri, HttpMethod.GET, new HttpEntity<>(headers), CarPathResponse.class
        ).getBody();
        if (carPathResponse.getRoutes() == null) {
            LoggingUtil.builder()
                    .title("차 길찾기")
                    .status("실패")
                    .message("지역: " + bestPlace.getPlaceName() + ", url: " + searchCarPathUri)
                    .build()
                    .print();
        } else {
            moveUserInfo = Optional.of(MoveUserInfo.createWithCarPath(group, participation, carPathResponse, bestPlace));
        }
        return moveUserInfo;
    }

    @TimeCheck
    public Optional<MoveUserInfo> getPublicRoute(
            BestPlace bestPlace, Space space, Participation participation
    ) {
        log.info("[getPublicRoute {} START]", participation.getUserName());
        ResponseEntity<TmapPublicPathResponse> response = restTemplate.postForEntity(
                tmapProperties.getSearchPathUri(),
                createTmapPublicRouteHttpEntity(bestPlace, participation),
                TmapPublicPathResponse.class
        );
        TmapPublicPathResponse tmapPublicPathResponse = response.getBody();

        if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
            MoveUserInfo moveUserInfo = tmapService.createMoveUserInfoWithPublicPath(space, participation, tmapPublicPathResponse, bestPlace);
            return Optional.of(moveUserInfo);
        }
        LoggingUtil.builder()
                .title("TMAP 대중교통 길찾기")
                .status("실패")
                .message("bestplace: " + bestPlace + ", participation: " + participation)
                .build()
                .print();

        return Optional.empty();
    }

    @TimeCheck
    public Optional<MoveUserInfo> getWalkRoute(
            BestPlace bestPlace, Space space, Participation participation
    ) {
        log.info("[getWalkRoute {} START]", participation.getUserName());
        ResponseEntity<TmapWalkPathResponse> response = restTemplate.postForEntity(
                tmapProperties.getWalkSearchPathUri(),
                createTmapWalkRouteHttpEntity(bestPlace, participation),
                TmapWalkPathResponse.class
        );
        if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
            MoveUserInfo moveUserInfo = tmapService.createMoveUserInfoWithWalkPath(space, participation, response.getBody(), bestPlace);
            return Optional.of(moveUserInfo);

        }
        LoggingUtil.builder()
                .title("TMAP 도보 길찾기")
                .status("실패")
                .message("bestplace: " + bestPlace + ", participation: " + participation)
                .build()
                .print();
        return Optional.empty();
    }

    private HttpEntity<?> createTmapPublicRouteHttpEntity(BestPlace bestPlace, Participation participation) {
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

    private HttpEntity<?> createTmapWalkRouteHttpEntity(BestPlace bestPlace, Participation participation) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("appKey", tmapProperties.getAppKey());

        String defaultLocationName = "%EC%84%B1%EC%8B%A0%EC%97%AC%EB%8C%80"; // 성신여대
        String startName = defaultLocationName; // 이름은 식별용으로 경로 탐색에 영향을 주지는 않음
        String endName = defaultLocationName;
        try {
            startName = URLEncoder.encode(participation.getLocationName(), "UTF-8");
            endName = URLEncoder.encode(bestPlace.getPlaceName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("보행자 경로 계산 시 출발지와 도착지 인코딩 중 에러발생: {}", e.getMessage());
        }
        Map<String, String> requestBody = tmapProperties.getWalkRequestParameter(
                startName,
                endName,
                participation.getLongitude(),
                participation.getLatitude(),
                bestPlace.getLongitude(),
                bestPlace.getLatitude()
        );
        return new HttpEntity<>(requestBody, headers);
    }
}