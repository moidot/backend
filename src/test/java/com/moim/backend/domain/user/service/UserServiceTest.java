package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.config.Platform;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.domain.user.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.moim.backend.domain.user.config.Platform.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean(value = {GoogleLoginService.class})
    private OAuth2LoginService oAuth2LoginService;

    @DisplayName("유저가 소셜 로그인을 진행한다.")
    @Test
    void loginByOAuth() {
        // given
        given(oAuth2LoginService.supports()).willReturn(GOOGLE);
        given(oAuth2LoginService.toEntityUser(anyString(), any(Platform.class)))
                .willReturn(
                        Users.builder()
                                .email("test@test.com")
                                .name("소셜테스트")
                                .build()
                );

        // when
        UserResponse.Login response = userService.loginByOAuth(
                "4%2F0AZEOvhX5fdg6aes78aDsv-H_pxySawXSIiwMOqtbOW3kt6tSxtnSd6_PVOpoemsJF9Q", GOOGLE
        );

        // then
        assertThat(response.getToken()).isNotNull();

        assertThat(response)
                .extracting("email", "name")
                .contains("test@test.com", "소셜테스트");
    }

}