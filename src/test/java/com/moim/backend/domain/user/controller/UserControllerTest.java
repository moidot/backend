package com.moim.backend.domain.user.controller;

import com.moim.backend.domain.ControllerTestSupport;
import com.moim.backend.domain.user.request.UserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends ControllerTestSupport {

    @DisplayName("이메일로 로그인")
    @Test
    void loginByEmail() throws Exception {
        // given
        UserRequest.Login request = new UserRequest.Login("yujung-31476@naver.com", "김유정");

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/user/login")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}
