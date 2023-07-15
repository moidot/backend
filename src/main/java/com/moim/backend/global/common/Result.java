package com.moim.backend.global.common;

import lombok.Getter;

@Getter
public enum Result {

    OK(0, "성공"),
    FAIL(-1, "실패"),
    // Space
    UNEXPECTED_EXCEPTION(-1000, "예상치 못한 예외가 발생했습니다."),
    NOT_FOUND_GROUP(-1001, "존재하지 않는 그룹입니다."),
    INVALID_TRANSPORTATION(-1002, "잘못된 이동수단 입니다.");

    private final int code;
    private final String message;

    Result(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
