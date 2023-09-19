package com.moim.backend.global.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.moim.backend.domain..controller.*.*(..))")
    public void beforeController(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        System.out.println("Entering method [" + className + "." + methodName + "]");
    }

    @After("execution(* com.moim.backend.domain..controller.*.*(..))")
    public void afterController(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        System.out.println("Exiting method [" + className + "." + methodName + "]");
    }
}
