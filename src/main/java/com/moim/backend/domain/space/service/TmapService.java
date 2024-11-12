package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.response.MoveUserInfo;
import com.moim.backend.domain.space.response.PathDto;
import com.moim.backend.domain.space.response.TmapPublicPathResponse;
import com.moim.backend.domain.space.response.TmapWalkPathResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TmapService {

    public MoveUserInfo createMoveUserInfoWithPublicPath(
            Space space,
            Participation participation,
            TmapPublicPathResponse tmapPublicPathResponse,
            BestPlace bestPlace
    ) {
        List<PathDto> path = new ArrayList<>();
        List<TmapPublicPathResponse.Leg> pathList = tmapPublicPathResponse.getPathList();
        // Tmap 대중교통 응답 값에서 도보와 지하철, 버스 등 이동 수단 별로 구분되어 있는 경로를 하나로 병합
        for (TmapPublicPathResponse.Leg leg : pathList) {
            if (leg.getSteps() != null && leg.getSteps().size() > 0) {
                path.addAll(getPathFromStep(leg));
            }
            if (leg.getStationList() != null && leg.getStationList().size() > 0) {
                path.addAll(getPathFromPassStopList(leg));
            }
        }
        path.add(new PathDto(bestPlace.getLongitude(), bestPlace.getLatitude()));
        return MoveUserInfo.createWithPublicPath(space, participation, tmapPublicPathResponse, path);
    }

    public MoveUserInfo createMoveUserInfoWithWalkPath(
            Space space,
            Participation participation,
            TmapWalkPathResponse tmapWalkPathResponse,
            BestPlace bestPlace
    ) {
        Set<PathDto> path = new LinkedHashSet<>();
        for (TmapWalkPathResponse.Feature feature : tmapWalkPathResponse.getFeatures()) {
            // path 구성 추가
            if ("Point".equals(feature.getGeometry().type)) {
                Optional<PathDto> pathFromPointOfWalkPath = getPathFromPointOfWalkPath(feature.geometry.coordinates);
                pathFromPointOfWalkPath.ifPresent(p -> path.add(p));
            } else if ("LineString".equals(feature.getGeometry().type)) {
                path.addAll(getPathListFromLineStringOfWalkPath(feature));
            }
        }
        path.add(new PathDto(bestPlace.getLongitude(), bestPlace.getLatitude()));
        return MoveUserInfo.createWithWalkPath(space, participation, tmapWalkPathResponse, new ArrayList<>(path));
    }

    // 이동 구간이 도보인 경우, 상세 도보 경로를 path list로 변환
    private List<PathDto> getPathFromStep(TmapPublicPathResponse.Leg leg) {
        List<PathDto> pathList = new ArrayList<>();
        for (TmapPublicPathResponse.Step step : leg.getSteps()) {
            String[] xyList = step.getLinestring().split(" ");
            for (String xy : xyList) {
                String[] splitXY = xy.split(",");
                pathList.add(PathDto.builder()
                        .longitude(Double.valueOf(splitXY[0]))
                        .latitude(Double.valueOf(splitXY[1]))
                        .build());
            }
        }
        return pathList;
    }

    // 이동 구간이 도보가 아니라 역으로 구성된 경우, 상세 경로를 path list로 변환
    private List<PathDto> getPathFromPassStopList(TmapPublicPathResponse.Leg leg) {
        List<PathDto> pathList = new ArrayList<>();
        for (TmapPublicPathResponse.Station station : leg.getStationList()) {
            pathList.add(PathDto.builder()
                    .longitude(Double.valueOf(station.getLon()))
                    .latitude(Double.valueOf(station.getLat()))
                    .build());
        }
        return pathList;
    }

    private List<PathDto> getPathListFromLineStringOfWalkPath(TmapWalkPathResponse.Feature feature) {
        List pathList = new ArrayList<>();
        for (Object coordinate : feature.geometry.coordinates) {
            if (coordinate instanceof List<?>) {
                Optional<PathDto> pathFromPoint = getPathFromPointOfWalkPath((List<Object>) coordinate);
                pathFromPoint.ifPresent(p -> pathList.add(p));
            }
        }
        return pathList;
    }

    private Optional<PathDto> getPathFromPointOfWalkPath(List<Object> point) {
        if (point.get(0) instanceof Double && point.get(1) instanceof Double) {
            PathDto path = PathDto.builder()
                    .longitude(Double.parseDouble(point.get(0).toString()))
                    .latitude(Double.parseDouble(point.get(1).toString()))
                    .build();
            return Optional.ofNullable(path);
        }
        return Optional.empty();
    }
}
