package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.response.PathDto;
import com.moim.backend.domain.space.response.TmapPublicPathResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TmapService {

    // Tmap 대중교통 응답 값에서 도보와 지하철, 버스 등 이동 수단 별로 구분되어 있는 경로를 하나로 병합
    public List<PathDto> getPath(TmapPublicPathResponse tmapPublicPathResponse) {
        List<PathDto> path = new ArrayList<>();
        List<TmapPublicPathResponse.Leg> pathList = tmapPublicPathResponse.getPathList();
        for (TmapPublicPathResponse.Leg leg : pathList) {
            if (leg.getSteps() != null && leg.getSteps().size() > 0) {
                path.addAll(getPathFromStep(leg));
            }
            if (leg.getStationList() != null && leg.getStationList().size() > 0) {
                path.addAll(getPathFromPassStopList(leg));
            }
        }
        return path;
    }

    // 이동 구간이 도보인 경우, 상세 도보 경로를 path list로 변환
    private List<PathDto> getPathFromStep(TmapPublicPathResponse.Leg leg) {
        List<PathDto> path = new ArrayList<>();
        for (TmapPublicPathResponse.Step step : leg.getSteps()) {
            String[] xyList = step.getLinestring().split(" ");
            for (String xy : xyList) {
                String[] splitXY = xy.split(",");
                path.add(PathDto.builder()
                        .longitude(Double.valueOf(splitXY[0]))
                        .latitude(Double.valueOf(splitXY[1]))
                        .build());
            }
        }
        return path;
    }

    // 이동 구간이 도보가 아니라 역으로 구성된 경우, 상세 경로를 path list로 변환
    private List<PathDto> getPathFromPassStopList(TmapPublicPathResponse.Leg leg) {
        List<PathDto> path = new ArrayList<>();
        for (TmapPublicPathResponse.Station station : leg.getStationList()) {
            path.add(PathDto.builder()
                    .longitude(Double.valueOf(station.getLon()))
                    .latitude(Double.valueOf(station.getLat()))
                    .build());
        }
        return path;
    }
}
