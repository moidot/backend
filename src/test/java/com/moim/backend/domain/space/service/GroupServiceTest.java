package com.moim.backend.domain.space.service;

import com.moim.backend.domain.space.request.GroupRequest;
import com.moim.backend.domain.space.response.GroupResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GroupServiceTest {

    @Autowired
    private GroupService groupService;

    @DisplayName("하나의 유저가 새로운 모임을 생성한다.")
    @Test
    void create() {
        // given
        GroupRequest.Create request = new GroupRequest.Create(
                "테스트 그룹", LocalDateTime.of(2023, 7, 14, 15, 0)
        );

        // when
        GroupResponse.Create response = groupService.createGroup(request.toServiceRequest());

        // then
        assertThat(response)
                .extracting("groupId", "adminId", "name", "date", "fixedPlace")
                .contains(1L, response.getAdminId(), "테스트 그룹", "2023-07-14", "none");
    }
}