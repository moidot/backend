package com.moim.backend.global.common;

import lombok.Getter;

@Getter
public enum RedisKeyPrefix {

    REFRESH_TOKEN("refresh_");

    private String prefix;

    RedisKeyPrefix(String prefix) {
        this.prefix = prefix;
    }

}
