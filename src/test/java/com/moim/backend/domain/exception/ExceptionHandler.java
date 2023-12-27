package com.moim.backend.domain.exception;

import com.moim.backend.global.common.Result;
import org.springframework.stereotype.Service;

@Service
public class ExceptionHandler {
    public String commonException() {
        return "email은 null이 될 수 없습니다.";
    }

    public Result customException() {
        return Result.NOT_FOUND_GROUP;
    }
}
