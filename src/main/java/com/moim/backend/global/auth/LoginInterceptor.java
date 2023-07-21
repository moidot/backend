package com.moim.backend.global.auth;

import com.moim.backend.global.auth.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

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

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null) {
            String token = jwtService.getToken(Optional.of(authorization));
            return jwtService.isValidated(token);
        }

        return true;
    }

}
