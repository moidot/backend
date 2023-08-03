package com.moim.backend.domain.groupvote.service;

import com.moim.backend.domain.groupvote.request.VoteRequest;
import com.moim.backend.domain.groupvote.response.VoteResponse;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.repository.BestPlaceRepository;
import com.moim.backend.domain.space.repository.GroupRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import com.moim.backend.global.common.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VoteServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParticipationRepository participationRepository;
    @Autowired
    private BestPlaceRepository bestPlaceRepository;
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private VoteService voteService;

    @DisplayName("모임장이 종료날짜를 정하지 않고 투표를 생성한다.")
    @Test
    void CreateVoteWithNotInsertDate() {

        // given
        Users admin = savedUser("admin@admin.com", "어드민");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");
        VoteRequest.Create request = new VoteRequest.Create(true, true, null);

        // when
        VoteResponse.Create response =
                voteService.createVote(request.toServiceRequest(), group.getGroupId(), admin);

        // then

        assertThat(response)
                .extracting("groupId", "isClosed", "isAnonymous", "isEnabledMultipleChoice", "endAt")
                .contains(group.getGroupId(), false, true, true, "none");
    }

    @DisplayName("모임장이 종료날짜를 정하고 투표를 생성한다.")
    @Test
    void CreateVoteWithInsertDate() {

        // given
        Users admin = savedUser("admin@admin.com", "어드민");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");
        VoteRequest.Create request =
                new VoteRequest.Create(false, false, LocalDateTime.of(2023, 8, 10, 15, 0, 0));

        // when
        VoteResponse.Create response =
                voteService.createVote(request.toServiceRequest(), group.getGroupId(), admin);

        // then

        assertThat(response)
                .extracting("groupId", "isClosed", "isAnonymous", "isEnabledMultipleChoice", "endAt")
                .contains(group.getGroupId(), false, false, false, "2023-08-10 15:00:00");
    }

    @DisplayName("모임장이 아닌 유저가 투표를 생성하려하면 Exception 이 발생한다.")
    @Test
    void CreateVoteWithNotAdminThrowException() {

        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");
        VoteRequest.Create request =
                new VoteRequest.Create(false, false, LocalDateTime.of(2023, 8, 10, 15, 0, 0));

        // when // then
        assertThatThrownBy(() -> voteService.createVote(request.toServiceRequest(), group.getGroupId(), user))
                .extracting("result.code", "result.message")
                .contains(Result.NOT_ADMIN_USER.getCode(), Result.NOT_ADMIN_USER.getMessage());

    }


    // method

    private void saveBestPlace(Groups group, String placeName, double longitude, double latitude) {
        bestPlaceRepository.save(
                BestPlace.builder()
                        .group(group)
                        .placeName(placeName)
                        .longitude(longitude)
                        .latitude(latitude)
                        .build()
        );
    }

    private Groups savedGroup(Long userId, String name) {
        return groupRepository.save(
                Groups.builder()
                        .adminId(userId)
                        .name(name)
                        .place("none")
                        .date(LocalDate.of(2023, 7, 10))
                        .build()
        );
    }

    private Participation savedParticipation(
            Users user, Groups group, String userName,
            String locationName, Double latitude, Double longitude,
            String type
    ) {
        return participationRepository.save(
                Participation.builder()
                        .group(group)
                        .userId(user.getUserId())
                        .userName(userName)
                        .locationName(locationName)
                        .latitude(latitude)
                        .longitude(longitude)
                        .transportation(TransportationType.valueOf(type))
                        .build()
        );
    }

    private Users savedUser(String email, String name) {
        return userRepository.save(
                Users.builder()
                        .email(email)
                        .name(name)
                        .build()
        );
    }
}