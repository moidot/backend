package com.moim.backend.global.auth;

import com.moim.backend.domain.user.entity.User;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.global.auth.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginAnnotation = parameter.getParameterAnnotation(Login.class) != null;
        boolean isUserClass = parameter.getParameterType().equals(User.class);

        return isLoginAnnotation && isUserClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String token = jwtService.getToken(webRequest);
        String email = jwtService.getUserEmail(token);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 존재하지 않습니다. email=" + email));
    }

}
