package com.moim.backend.domain.subway.repository;

import com.moim.backend.TestQueryDSLConfig;
import com.moim.backend.global.dto.BestRegion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestQueryDSLConfig.class)
@Transactional
class SubwayRepositoryTest {

    @Autowired
    private SubwayRepository subwayRepository;

    @DisplayName("주어진 위치에서 가장 가까운 역을 찾는다.")
    @Test
    void getNearestStations() {
        // when
        List<BestRegion> subwayList = subwayRepository.getNearestStationsList(37.498085, 127.027706);

        // then
        assertThat(subwayList)
                .hasSize(3)
                .extracting("name")
                .contains("강남", "신논현", "역삼");
    }
}