package com.moim.backend.domain.space.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpaceFilterEnum {
    ABC("가나다순"),
    LATEST("최신 순"),
    OLDEST("오래된 순");

    private final String name;
}
