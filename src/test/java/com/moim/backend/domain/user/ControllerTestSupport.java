package com.moim.backend.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moim.backend.domain.user.controller.UserController;
import com.moim.backend.domain.user.service.UserService;
import com.moim.backend.global.auth.LoginInterceptor;
import com.moim.backend.global.config.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        UserController.class
}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class, LoginInterceptor.class}
))
@AutoConfigureMockMvc(addFilters = false)
public class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected UserController userController;

    @MockBean
    protected UserService userService;

}
