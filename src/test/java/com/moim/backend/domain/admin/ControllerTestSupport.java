package com.moim.backend.domain.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moim.backend.domain.admin.controller.VersionController;
import com.moim.backend.domain.admin.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        VersionController.class
})
@AutoConfigureMockMvc(addFilters = false)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected VersionController versionController;

    @MockBean
    protected VersionService versionService;
}
