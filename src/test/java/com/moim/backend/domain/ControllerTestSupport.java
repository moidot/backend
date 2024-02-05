package com.moim.backend.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moim.backend.domain.bookmark.controller.BookmarkController;
import com.moim.backend.domain.spacevote.controller.VoteController;
import com.moim.backend.domain.space.controller.SpaceController;
import com.moim.backend.domain.user.controller.UserCalendarController;
import com.moim.backend.domain.user.controller.UserController;
import com.moim.backend.global.auth.LoginArgumentResolver;
import com.moim.backend.global.auth.LoginInterceptor;
import com.moim.backend.global.auth.jwt.JwtService;
import com.moim.backend.global.config.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        SpaceController.class,
        UserController.class,
        VoteController.class,
        BookmarkController.class,
        UserCalendarController.class
}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class, LoginInterceptor.class}
))
@AutoConfigureMockMvc(addFilters = false)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected SpaceController spaceController;

    @MockBean
    protected UserController userController;

    @MockBean
    protected UserCalendarController userCalendarController;

    @MockBean
    protected VoteController voteController;

    @MockBean
    protected BookmarkController bookmarkController;

    @MockBean
    protected LoginArgumentResolver loginArgumentResolver;

    @MockBean
    protected JwtService jwtService;
}
