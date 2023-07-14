package com.moim.backend.domain.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moim.backend.domain.admin.controller.VersionController;
import com.moim.backend.domain.admin.service.VersionService;
import com.moim.backend.domain.space.controller.GroupController;
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
        VersionController.class,
        GroupController.class
}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class, LoginInterceptor.class}
))
@AutoConfigureMockMvc(addFilters = false)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected VersionController versionController;

    @MockBean
    protected GroupController groupController;
}
