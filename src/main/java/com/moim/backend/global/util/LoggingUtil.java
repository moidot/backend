package com.moim.backend.global.util;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class LoggingUtil {
    private String title;
    private String status;
    private String message;

    public void print() {
        log.info("[ {} {} ] ========================================", title, status);
        log.info(message);
        log.info("=================================================================");
    }
}
