package com.moim.backend.global.common.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.moim.backend.global.common.CustomResponseEntity;
import com.moim.backend.global.common.Result;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.List;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class CommonRestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public CustomResponseEntity<String> handleExceptionHandler(HttpServletRequest request, Exception e) {
        log.error("defaultExceptionHandler", e);
        return CustomResponseEntity.fail(Result.UNEXPECTED_EXCEPTION);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomException.class)
    public CustomResponseEntity<String> handleCustomExceptionHandler(CustomException exception) {
        log.error("CustomExceptionHandler code : {}, message : {}",
                exception.getResult().getCode(), exception.getResult().getMessage());
        return CustomResponseEntity.fail(exception.getResult());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public CustomResponseEntity<String> illegalArgumentExceptionHandler(
            IllegalArgumentException e, HttpServletRequest request
    ) {
        log.error("url: \"{}\", message: {}", request.getRequestURI(), e.getMessage());

        return CustomResponseEntity.fail(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public CustomResponseEntity<Object> handleBadRequest(
            MethodArgumentNotValidException e, HttpServletRequest request
    ) {
        FieldError error = e.getBindingResult().getFieldErrors().get(0);
        String errorMessage = "[" + error.getField() + "] " + error.getDefaultMessage();
        log.error("url: \"{}\", message: {}", request.getRequestURI(), errorMessage);

        return CustomResponseEntity.fail(errorMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
            MissingServletRequestParameterException.class
    )
    public CustomResponseEntity<String> handleBadRequest(
            MissingServletRequestParameterException e, HttpServletRequest request
    ) {
        String errorMessage = e.getParameterName() + " 값이 등록되지 않았습니다.";
        log.error("url: \"{}\", message: {}", request.getRequestURI(), errorMessage);

        return CustomResponseEntity.fail(errorMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
            MissingServletRequestPartException.class
    )
    public CustomResponseEntity<String> handleBadRequest(
            MissingServletRequestPartException e, HttpServletRequest request
    ) {
        String errorMessage = e.getRequestPartName() + " 값을 요청받지 못했습니다.";
        log.error("url: \"{}\", message: {}", request.getRequestURI(), errorMessage);

        return CustomResponseEntity.fail(errorMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullPointerException.class)
    public CustomResponseEntity<String> nullPointerExceptionHandler(
            Exception e, HttpServletRequest request
    ) {
        return CustomResponseEntity.fail(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
            {SecurityException.class, MalformedJwtException.class}
    )
    public CustomResponseEntity<String> securityExceptionHandler(
            SecurityException e, HttpServletRequest request
    ) {
        String errorMessage = String.format("잘못된 JWT 서명입니다. | url: %s, message: %s", request.getRequestURI(), e.getMessage());
        return handleException(errorMessage, e, request);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public CustomResponseEntity<String> expiredJwtExceptionHandler(
            ExpiredJwtException e, HttpServletRequest request
    ) {
        String errorMessage = "토큰이 만료되었습니다.";
        return handleException(errorMessage, e, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnsupportedJwtException.class)
    public CustomResponseEntity<String> unsupportedJwtExceptionHandler(
            ExpiredJwtException e, HttpServletRequest request
    ) {
        String errorMessage = "지원되지 않는 JWT 토큰입니다.";
        return handleException(errorMessage, e, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JsonParseException.class)
    public CustomResponseEntity<String> JsonParseExceptionHandler(
            ExpiredJwtException e, HttpServletRequest request
    ) {
        String errorMessage = "지원되지 않는 JSON 형식입니다..";
        return handleException(errorMessage, e, request);
    }

    // 기본 예외 핸들러
    private CustomResponseEntity<String> handleException(Throwable e) {
        return handleException(e.getMessage(), e);
    }

    private CustomResponseEntity<String> handleException(String errorMessage, Throwable e, HttpServletRequest request) {
        log.error("url: {}", request.getRequestURI());
        return handleException(errorMessage, e);
    }

    private CustomResponseEntity<String> handleException(String errorMessage, Throwable e) {
        printErrorLog(e);
        if (e.getCause() != null) {
            log.error("▼ [Cause of Error] ▼");
            printErrorLog(e.getCause());
        }

        return CustomResponseEntity.fail(errorMessage);
    }

    private void printErrorLog(Throwable e) {
        StackTraceElement lastOfErrorStack = e.getStackTrace()[0];
        String lastOfErrorClassName = lastOfErrorStack.getClassName();
        String lastOfErrorMethodName = lastOfErrorStack.getMethodName();
        int lastOfErrorLineNumber = lastOfErrorStack.getLineNumber();

        log.error("{} in {}.{} at line {}", e.getClass(), lastOfErrorClassName, lastOfErrorMethodName, lastOfErrorLineNumber);
        log.error("Exception message: {}", e.getMessage());
    }


}