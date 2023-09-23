package com.moim.backend.domain.user.controller;

import com.moim.backend.domain.ControllerTestSupport;
import com.moim.backend.domain.user.request.UserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends ControllerTestSupport {

    @DisplayName("소셜 로그인 API")
    @Test
    void loginByOAuth() throws Exception {
        // given

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/auth/signin")
                                .param("code", "Hx-PXmWuFaGakYCEy8hkUIVOWUSXIOtD7cosKDSIKsiwodR1g35KXQQWX9H4hXlcpZ45eSgo3dGkWWWOSX-z9iQ")
                                .param("platform", "NAVER")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그아웃 API")
    @Test
    void logout() throws Exception {
        // given

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/auth/logout")
                                .header(AUTHORIZATION, "Bearer {token}")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}
