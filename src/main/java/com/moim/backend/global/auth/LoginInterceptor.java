package com.moim.backend.global.auth;

import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.auth.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
            return true;
        }

        if (isNeedLogin(handler)) {
            String token = jwtService.getToken(request);
            return jwtService.isValidated(token);
        }

        return true;
    }

    private boolean isNeedLogin(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        return Arrays.stream(handlerMethod.getMethodParameters()).anyMatch(parameter -> {
            boolean isLoginAnnotation = parameter.getParameterAnnotation(Login.class) != null;
            boolean isUserClass = parameter.getParameterType().equals(Users.class);

            return isLoginAnnotation && isUserClass;
        });
    }

}
