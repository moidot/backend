package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.Space;
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
public class MoveUserInfo {
    @Schema(description = "모임장 여부")
    private Boolean isAdmin;
    @Schema(description = "사용자 아이디")
    private Long userId;
    @Schema(description = "사용자 닉네임")
    private String userName;
    @Schema(description = "이동 수단", allowableValues = {"PUBLIC", "PERSONAL", "WALK"})
    private TransportationType transportationType;
    @Schema(description = "총 환승 횟수")
    private int transitCount;
    @Schema(description = "이동 시간. 단위: 분")
    private int totalTime;
    @Schema(description = "총 이동 거리 단위: 미터")
    private Double totalDistance;
    @Schema(description = "예상 요금")
    private int payment;
    @Schema(description = "이동 경로에 따른 좌표 리스트. 경로를 찾을 수 없을 경우, 비어있는 리스트로 반환")
    private List<PathDto> path = new ArrayList<>();

    // 경로를 찾을 수 없는 사용자
    public static MoveUserInfo createWithEmptyPath(
            Space space,
            Participation participation
    ) {
        MoveUserInfo moveUserInfo = new MoveUserInfo();
        moveUserInfo.isAdmin = (participation.getUserId() == space.getAdminId()) ? true : false;
        moveUserInfo.userId = participation.getUserId();
        moveUserInfo.transportationType = participation.getTransportation();
        moveUserInfo.userName = participation.getUserName();
        return moveUserInfo;
    }

    public static MoveUserInfo createWithPublicPath(
            Space space,
            Participation participation,
            TmapPublicPathResponse tmapPublicPathResponse,
            List<PathDto> path
    ) {
        MoveUserInfo moveUserInfo = new MoveUserInfo();
        moveUserInfo.isAdmin = (participation.getUserId() == space.getAdminId()) ? true : false;
        moveUserInfo.userId = participation.getUserId();
        moveUserInfo.userName = participation.getUserName();
        moveUserInfo.transportationType = participation.getTransportation();
        moveUserInfo.totalTime = tmapPublicPathResponse.getTotalTime();
        moveUserInfo.totalDistance = tmapPublicPathResponse.getTotalDistance();
        moveUserInfo.path = path;
        moveUserInfo.payment = tmapPublicPathResponse.getPayment();
        return moveUserInfo;
    }

    public static MoveUserInfo createWithCarPath(
            Space space,
            Participation participation,
            CarPathResponse carPathResponse,
            BestPlace bestPlace
    ) {
        MoveUserInfo moveUserInfo = new MoveUserInfo();
        moveUserInfo.isAdmin = (participation.getUserId() == space.getAdminId()) ? true : false;
        moveUserInfo.userId = participation.getUserId();
        moveUserInfo.userName = participation.getUserName();
        moveUserInfo.transportationType = participation.getTransportation();
        moveUserInfo.totalTime = carPathResponse.getTotalTime();
        moveUserInfo.totalDistance = carPathResponse.getTotalDistance();
        moveUserInfo.path = carPathResponse.getPathList(participation, bestPlace);
        moveUserInfo.payment = carPathResponse.getPayment();
        return moveUserInfo;
    }

    public static MoveUserInfo createWithWalkPath(
            Space space,
            Participation participation,
            TmapWalkPathResponse tmapWalkPathResponse,
            List<PathDto>path
    ) {
        MoveUserInfo moveUserInfo = new MoveUserInfo();
        moveUserInfo.isAdmin = (participation.getUserId() == space.getAdminId()) ? true : false;
        moveUserInfo.userId = participation.getUserId();
        moveUserInfo.userName = participation.getUserName();
        moveUserInfo.transportationType = TransportationType.WALK;
        moveUserInfo.totalTime = tmapWalkPathResponse.getTotalTime();
        moveUserInfo.totalDistance = tmapWalkPathResponse.getTotalDistance();
        moveUserInfo.path = path;
        moveUserInfo.payment = tmapWalkPathResponse.getPayment();
        return moveUserInfo;
    }
}
