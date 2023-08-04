package com.moim.backend.domain.groupvote.service;

import com.moim.backend.domain.groupvote.entity.SelectPlace;
import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.groupvote.repository.SelectPlaceRepository;
import com.moim.backend.domain.groupvote.repository.VoteRepository;
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
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

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
    private VoteRepository voteRepository;
    @Autowired
    private SelectPlaceRepository selectPlaceRepository;
    @Autowired
    private EntityManager em;

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


    @DisplayName("유저가 투표에서 하나의 장소를 선택한다.")
    @Test
    void selectVoteWithOnlyOneUser() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");
        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);
        Vote vote = saveVote(group.getGroupId(), true, false, null);

        em.flush();
        em.clear();

        // when
        VoteResponse.SelectResult response =
                voteService.selectVote(group.getGroupId(), List.of(bestPlace1.getBestPlaceId()), user, LocalDateTime.now());

        // then

        assertThat(response)
                .extracting("groupId", "voteId", "groupName", "groupDate")
                .contains(group.getGroupId(), vote.getVoteId(), group.getName(), "2023-07-10");

        assertThat(response.getVoteStatuses())
                .extracting("bestPlaceId", "votes", "placeName", "isVoted")
                .contains(
                        tuple(bestPlace1.getBestPlaceId(), 1, "강남역", true),
                        tuple(bestPlace2.getBestPlaceId(), 0, "역삼역", false),
                        tuple(bestPlace3.getBestPlaceId(), 0, "신논현역", false)
                );
    }

    @DisplayName("유저가 투표에서 두개의 장소를 선택한다.")
    @Test
    void selectVoteWithMultipleChoiceUser() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");
        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);
        Vote vote = saveVote(group.getGroupId(), true, true, null);

        em.flush();
        em.clear();

        // when
        VoteResponse.SelectResult response =
                voteService.selectVote(
                        group.getGroupId(),
                        List.of(bestPlace1.getBestPlaceId(), bestPlace3.getBestPlaceId()),
                        user,
                        LocalDateTime.now()
                );

        // then

        assertThat(response)
                .extracting("groupId", "voteId", "groupName", "groupDate")
                .contains(group.getGroupId(), vote.getVoteId(), group.getName(), "2023-07-10");

        assertThat(response.getVoteStatuses())
                .extracting("bestPlaceId", "votes", "placeName", "isVoted")
                .contains(
                        tuple(bestPlace1.getBestPlaceId(), 1, "강남역", true),
                        tuple(bestPlace2.getBestPlaceId(), 0, "역삼역", false),
                        tuple(bestPlace3.getBestPlaceId(), 1, "신논현역", true)
                );
    }

    @DisplayName("유저가 투표에서 두개의 장소를 선택할때, 투표 종료시간이 이미 지나 Exception 이 발생한다.")
    @Test
    void selectVoteAfterEndAtThrowException() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");
        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);
        Vote vote = saveVote(group.getGroupId(), true, true, LocalDateTime.of(2023, 8, 3, 12, 0, 0));

        System.out.println(LocalDateTime.now());
        em.flush();
        em.clear();

        // when // then
        assertThatThrownBy(() -> voteService.selectVote(
                group.getGroupId(),
                List.of(bestPlace1.getBestPlaceId(), bestPlace3.getBestPlaceId()),
                user,
                LocalDateTime.now()))
                .extracting("result.code", "result.message")
                .contains(-2005, "해당 투표는 종료 시간이 지났습니다.");
    }

    private Vote saveVote(
            Long groupId, Boolean isAnonymous, boolean isEnabledMultipleChoice, LocalDateTime endAt
    ) {
        return voteRepository.save(
                Vote.builder()
                        .groupId(groupId)
                        .isClosed(false)
                        .isAnonymous(isAnonymous)
                        .isEnabledMultipleChoice(isEnabledMultipleChoice)
                        .endAt(endAt)
                        .build()
        );
    }

    // method

    private SelectPlace saveSelectPlace(Users user, BestPlace bestPlace, Vote vote) {
        return selectPlaceRepository.save(
                SelectPlace.builder()
                        .vote(vote)
                        .userId(user.getUserId())
                        .bestPlace(bestPlace)
                        .build()
        );
    }

    private BestPlace saveBestPlace(Groups group, String placeName, double longitude, double latitude) {
        return bestPlaceRepository.save(
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