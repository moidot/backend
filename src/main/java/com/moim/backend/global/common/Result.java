package com.moim.backend.global.common;

import lombok.Getter;

@Getter
public enum Result {
    // 공통
    OK(0, "성공"),
    FAIL(-1, "실패"),

    // 소셜로그인 (네이버)
    INVALID_ACCESS_INFO(-900, "프로필을 요청하기위한 액세스 정보가 유효하지 않습니다."),
    NOT_FOUND_NAVER_LOGIN(-901, "네이버 로그인을 위한 접근 URL이 잘못되었습니다."),
    NOT_AUTHENTICATE_NAVER_TOKEN_INFO(-902, "토큰 정보 접근을 위한 권한이 잘못되었습니다."),

    // Space
    UNEXPECTED_EXCEPTION(-500,"예상치 못한 예외가 발생했습니다."),

    NOT_FOUND_GROUP(-1001,"존재하지 않는 그룹입니다."),

    INVALID_TRANSPORTATION(-1002,"잘못된 이동수단 입니다."),

    NOT_FOUND_PARTICIPATE(-1003,"존재하지 않는 참여자 정보 입니다."),

    NOT_MATCHED_PARTICIPATE(-1004,"자신의 참여 정보가 아닙니다."),

    NOT_ADMIN_USER(-1005,"해당 유저는 그룹의 어드민이 아닙니다."),
    DUPLICATE_PARTICIPATION(-1006, "동일한 유저가 이미 스페이스에 참여하고 있습니다."),
    NOT_REQUEST_NAVER(-1007, "네이버 API 요청에 실패하였습니다."),
    INCORRECT_LOCATION_NAME(-1009, "잘못된 지역 이름 입니다."),

    // Vote
    NOT_CREATED_VOTE(-2001, "해당 그룹은 투표가 개설되지 않았습니다."),
    NOT_FOUND_BESTPLACE(-2002, "존재하지 않는 추천 장소 입니다."),
    IS_CLOSED_VOTE(-2003, "해당 투표는 이미 종료되었습니다."),
    NOT_MULTIPLE_CHOICE(-2004, "해당 투표는 중복 선택이 허용되지 않습니다." ),
    VOTE_ALREADY_ENDED(-2005,"해당 투표는 종료 시간이 지났습니다." ),
    NOT_VOTED_PLACE(-2006, "해당 장소를 투표한 인원은 0명 입니다." ),
    ALREADY_CREATED_VOTE(-2007, "해당 모임은 이미 투표가 시작되었습니다." );

    private final int code;
    private final String message;

    Result(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
