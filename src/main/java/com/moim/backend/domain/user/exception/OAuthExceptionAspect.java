package com.moim.backend.domain.user.exception;

import com.moim.backend.global.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import static com.moim.backend.global.common.Result.*;

@Aspect
@Component
@Slf4j
public class OAuthExceptionAspect {

    @AfterThrowing(pointcut = "execution(* com.moim.backend.domain.user.service.*.*(..))", throwing = "exception")
    public void handleExceptions(Exception exception) {
        if (exception instanceof HttpStatusCodeException) {
            handleHttpExceptions((HttpStatusCodeException) exception);
        } else if (exception instanceof ResourceAccessException) {
            handleNetworkExceptions((ResourceAccessException) exception);
        } else if (exception instanceof HttpMessageNotReadableException) {
            handleResponseParseExceptions((HttpMessageNotReadableException) exception);
        }
    }

    private void handleHttpExceptions(HttpStatusCodeException e) {
        log.error("HTTP error occurred: {}", e.getStatusCode(), e);
        throw new CustomException(FAIL_REQUEST_ACCESS_TOKEN);
    }

    private void handleNetworkExceptions(ResourceAccessException e) {
        log.error("Network issue: {}", e.getMessage(), e);
        throw new CustomException(FAIL_REQUEST_TIME_OUT);
    }

    private void handleResponseParseExceptions(HttpMessageNotReadableException e) {
        log.error("Unparseable response body: {}", e.getMessage(), e);
        throw new CustomException(NOT_MATCH_RESPONSE);
    }
}
