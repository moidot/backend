package com.moim.backend.domain.admin.service;

import com.moim.backend.domain.admin.request.VersionRequest;
import com.moim.backend.domain.admin.response.VersionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class VersionServiceTest {
    @Autowired
    private VersionService versionService;

    @DisplayName("관리자가 서비스의 버전을 변경한다.")
    @Test
    void updateServiceVersion() {
        // given
        VersionRequest.Update request = new VersionRequest.Update("0.0.2", "TEST");

        // when
        VersionResponse.Update update =
                versionService.updateServiceVersion(request.toServiceRequest());

        // then
        assertThat(update)
                .extracting("version", "updateAdminName")
                .contains("0.0.2", "TEST");
    }

}