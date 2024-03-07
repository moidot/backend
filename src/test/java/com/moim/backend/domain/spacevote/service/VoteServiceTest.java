package com.moim.backend.domain.spacevote.service;

import com.moim.backend.domain.spacevote.entity.SelectPlace;
import com.moim.backend.domain.spacevote.entity.Vote;
import com.moim.backend.domain.spacevote.repository.SelectPlaceRepository;
import com.moim.backend.domain.spacevote.repository.VoteRepository;
import com.moim.backend.domain.spacevote.request.controller.VoteCreateRequest;
import com.moim.backend.domain.spacevote.response.VoteCreateResponse;
import com.moim.backend.domain.spacevote.response.VoteSelectPlaceUserResponse;
import com.moim.backend.domain.spacevote.response.VoteSelectResultResponse;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.repository.BestPlaceRepository;
import com.moim.backend.domain.space.repository.SpaceRepository;
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
    private SpaceRepository groupRepository;
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
        Users user = savedUser("test@test.com", "테스트");
        Space group = savedGroup(admin.getUserId(), "테스트 그룹");
        VoteCreateRequest request = VoteCreateRequest.toRequest(true, true, null);

        savedParticipation(admin, group, "어드민", "테스트", 123.123, 123.123, "PUBLIC");
        savedParticipation(user, group, "일반인", "테스트", 123.123, 123.123, "PUBLIC");

        em.flush();
        em.clear();

        // when
        VoteCreateResponse response =
                voteService.createVote(request, group.getSpaceId(), admin);

        // then

        assertThat(response)
                .extracting("groupId", "isClosed", "isAnonymous", "isEnabledMultipleChoice", "endAt")
                .contains(group.getSpaceId(), false, true, true, "none");
    }

    @DisplayName("모임장이 종료날짜를 정하고 투표를 생성한다.")
    @Test
    void CreateVoteWithInsertDate() {

        // given
        Users admin = savedUser("admin@admin.com", "어드민");
        Users user = savedUser("test@test.com", "테스트");
        Space group = savedGroup(admin.getUserId(), "테스트 그룹");
        VoteCreateRequest request = VoteCreateRequest.toRequest(
                false, false, LocalDateTime.of(2023, 8, 10, 15, 0, 0)
        );

        savedParticipation(admin, group, "어드민", "테스트", 123.123, 123.123, "PUBLIC");
        savedParticipation(user, group, "일반인", "테스트", 123.123, 123.123, "PUBLIC");

        em.flush();
        em.clear();

        // when
        VoteCreateResponse response =
                voteService.createVote(request, group.getSpaceId(), admin);

        // then

        assertThat(response)
                .extracting("groupId", "isClosed", "isAnonymous", "isEnabledMultipleChoice", "endAt")
                .contains(group.getSpaceId(), false, false, false, "2023-08-10T15:00:00");
    }

    @DisplayName("모임장이 아닌 유저가 투표를 생성하려하면 Exception 이 발생한다.")
    @Test
    void CreateVoteWithNotAdminThrowException() {

        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");
        Space group = savedGroup(admin.getUserId(), "테스트 그룹");
        VoteCreateRequest request = VoteCreateRequest.toRequest(
                false, false, LocalDateTime.of(2023, 8, 10, 15, 0, 0)
        );

        // when // then
        assertThatThrownBy(() -> voteService.createVote(request, group.getSpaceId(), user))
                .extracting("result.code", "result.message")
                .contains(Result.NOT_ADMIN_USER.getCode(), Result.NOT_ADMIN_USER.getMessage());

    }


    @DisplayName("유저가 투표에서 하나의 장소를 선택한다.")
    @Test
    void selectVoteWithOnlyOneUser() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");
        Space group = savedGroup(admin.getUserId(), "테스트 그룹");
        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);
        Vote vote = saveVote(group.getSpaceId(), true, false, null);

        em.flush();
        em.clear();

        // when
        VoteSelectResultResponse response =
                voteService.selectVote(group.getSpaceId(), List.of(bestPlace1.getBestPlaceId()), user, LocalDateTime.now());

        // then

        assertThat(response)
                .extracting("groupId", "voteId", "groupName", "groupDate")
                .contains(group.getSpaceId(), vote.getVoteId(), group.getName(), "2023-07-10");

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
        Space group = savedGroup(admin.getUserId(), "테스트 그룹");
        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);
        Vote vote = saveVote(group.getSpaceId(), true, true, null);

        em.flush();
        em.clear();

        // when
        VoteSelectResultResponse response =
                voteService.selectVote(
                        group.getSpaceId(),
                        List.of(bestPlace1.getBestPlaceId(), bestPlace3.getBestPlaceId()),
                        user,
                        LocalDateTime.now()
                );

        // then

        assertThat(response)
                .extracting("groupId", "voteId", "groupName", "groupDate")
                .contains(group.getSpaceId(), vote.getVoteId(), group.getName(), "2023-07-10");

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
        Space group = savedGroup(admin.getUserId(), "테스트 그룹");
        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);
        Vote vote = saveVote(group.getSpaceId(), true, true, LocalDateTime.of(2023, 8, 3, 12, 0, 0));

        em.flush();
        em.clear();

        // when // then
        assertThatThrownBy(() -> voteService.selectVote(
                group.getSpaceId(),
                List.of(bestPlace1.getBestPlaceId(), bestPlace3.getBestPlaceId()),
                user,
                LocalDateTime.now()))
                .extracting("result.code", "result.message")
                .contains(-2005, "해당 투표는 종료 시간이 지났습니다.");
    }

    @DisplayName("유저가 자신의 그룹의 투표현황을 확인한다.")
    @Test
    void readVote() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");

        Space group = savedGroup(admin.getUserId(), "테스트 그룹");

        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);

        Vote vote = saveVote(group.getSpaceId(), true, true, LocalDateTime.of(2023, 8, 3, 12, 0, 0));

        SelectPlace selectPlace1 = saveSelectPlace(user, bestPlace1, vote);
        SelectPlace selectPlace2 = saveSelectPlace(user, bestPlace3, vote);
        SelectPlace selectPlace3 = saveSelectPlace(admin, bestPlace1, vote);
        SelectPlace selectPlace4 = saveSelectPlace(admin, bestPlace2, vote);

        em.flush();
        em.clear();

        // when
        VoteSelectResultResponse response = voteService.readVote(group.getSpaceId(), admin);

        // then

        assertThat(response)
                .extracting("groupId", "voteId", "groupName", "groupDate", "endAt", "isVotingParticipant")
                .contains(group.getSpaceId(), vote.getVoteId(), group.getName(), "2023-07-10", "2023-08-03T12:00:00", true);

        assertThat(response.getVoteStatuses())
                .extracting("bestPlaceId", "votes", "placeName", "isVoted")
                .contains(
                        tuple(bestPlace1.getBestPlaceId(), 2, "강남역", true),
                        tuple(bestPlace2.getBestPlaceId(), 1, "역삼역", true),
                        tuple(bestPlace3.getBestPlaceId(), 1, "신논현역", false)
                );
    }

    @DisplayName("유저가 자신의 그룹의 투표를 하지 않은 상황에서 투표 현황을 확인한다.")
    @Test
    void readVote2() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");

        Space group = savedGroup(admin.getUserId(), "테스트 그룹");

        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);

        Vote vote = saveVote(group.getSpaceId(), true, true, LocalDateTime.of(2023, 8, 3, 12, 0, 0));

        SelectPlace selectPlace1 = saveSelectPlace(user, bestPlace1, vote);
        SelectPlace selectPlace2 = saveSelectPlace(user, bestPlace3, vote);

        em.flush();
        em.clear();

        // when
        VoteSelectResultResponse response = voteService.readVote(group.getSpaceId(), admin);

        // then

        assertThat(response)
                .extracting("groupId", "voteId", "groupName", "groupDate", "endAt", "isVotingParticipant")
                .contains(group.getSpaceId(), vote.getVoteId(), group.getName(), "2023-07-10", "2023-08-03T12:00:00", false);

        assertThat(response.getVoteStatuses())
                .extracting("bestPlaceId", "votes", "placeName", "isVoted")
                .contains(
                        tuple(bestPlace1.getBestPlaceId(), 1, "강남역", false),
                        tuple(bestPlace2.getBestPlaceId(), 0, "역삼역", false),
                        tuple(bestPlace3.getBestPlaceId(), 1, "신논현역", false)
                );
    }

    @DisplayName("유저가 자신의 그룹의 투표현황을 개설되지 않은채 확인한다.")
    @Test
    void readVoteWithNotCreateVote() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");

        Space group = savedGroup(admin.getUserId(), "테스트 그룹");

        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);

        em.flush();
        em.clear();

        // when
        VoteSelectResultResponse response = voteService.readVote(group.getSpaceId(), admin);

        // then

        assertThat(response)
                .extracting("groupId", "voteId", "groupName", "groupDate")
                .contains(group.getSpaceId(), -1L, group.getName(), "2023-07-10");

        assertThat(response.getVoteStatuses()).isEmpty();
    }


    @DisplayName("유저가 해당 장소에 투표한 사람들의 목록을 조회한다.")
    @Test
    void readSelectPlaceUsers() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");

        Space group = savedGroup(admin.getUserId(), "테스트 그룹");

        Participation par1 = savedParticipation(admin, group, "어드민", "테스트", 123.123, 123.123, "PUBLIC");
        Participation par2 = savedParticipation(user, group, "일반인", "테스트", 123.123, 123.123, "PUBLIC");

        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);

        Vote vote = saveVote(group.getSpaceId(), true, true, LocalDateTime.of(2023, 8, 3, 12, 0, 0));

        SelectPlace selectPlace1 = saveSelectPlace(user, bestPlace1, vote);
        SelectPlace selectPlace2 = saveSelectPlace(user, bestPlace3, vote);
        SelectPlace selectPlace3 = saveSelectPlace(admin, bestPlace1, vote);
        SelectPlace selectPlace4 = saveSelectPlace(admin, bestPlace2, vote);

        em.flush();
        em.clear();

        // when
        VoteSelectPlaceUserResponse response = voteService.readSelectPlaceUsers(
                group.getSpaceId(), bestPlace1.getBestPlaceId()
        );

        // then
        assertThat(response.getVoteParticipations())
                .extracting("participationId", "userId", "nickName", "isAdmin")
                .contains(
                        tuple(par1.getParticipationId(), par1.getUserId(), par1.getUserName(), true),
                        tuple(par2.getParticipationId(), par2.getUserId(), par2.getUserName(), false)

                );
    }

    @DisplayName("어드민이 자신의 그룹의 투표를 종료하고 가장 높은 선택을 받은 장소가 선정된다.")
    @Test
    void conclusionVote() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");

        Space group = savedGroup(admin.getUserId(), "테스트 그룹");

        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);

        Vote vote = saveVote(group.getSpaceId(), true, true, LocalDateTime.of(2023, 8, 3, 12, 0, 0));

        SelectPlace selectPlace1 = saveSelectPlace(user, bestPlace1, vote);
        SelectPlace selectPlace2 = saveSelectPlace(user, bestPlace3, vote);
        SelectPlace selectPlace3 = saveSelectPlace(admin, bestPlace1, vote);
        SelectPlace selectPlace4 = saveSelectPlace(admin, bestPlace2, vote);

        em.flush();
        em.clear();

        // when
        VoteSelectResultResponse response = voteService.conclusionVote(group.getSpaceId(), admin);

        // then
        assertThat(response)
                .extracting("groupId", "voteId", "groupName", "groupDate", "endAt", "isClosed", "confirmPlace")
                .contains(
                        group.getSpaceId(), vote.getVoteId(),
                        group.getName(), "2023-07-10", "2023-08-03T12:00:00", true, "강남역"
                );

        assertThat(response.getVoteStatuses())
                .extracting("bestPlaceId", "votes", "placeName", "isVoted")
                .contains(
                        tuple(bestPlace1.getBestPlaceId(), 2, "강남역", true),
                        tuple(bestPlace2.getBestPlaceId(), 1, "역삼역", true),
                        tuple(bestPlace3.getBestPlaceId(), 1, "신논현역", false)
                );

        em.flush();
        em.clear();

        Vote validateVote = voteRepository.findById(vote.getVoteId()).get();
        assertThat(validateVote.getIsClosed()).isTrue();
    }

    @DisplayName("일반 유저가 자신의 그룹의 투표를 종료하려할때 익셉션이 발생한다.")
    @Test
    void conclusionWithNotAdminThrowException() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");

        Space group = savedGroup(admin.getUserId(), "테스트 그룹");

        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);

        Vote vote = saveVote(group.getSpaceId(), true, true, LocalDateTime.of(2023, 8, 3, 12, 0, 0));

        SelectPlace selectPlace1 = saveSelectPlace(user, bestPlace1, vote);
        SelectPlace selectPlace2 = saveSelectPlace(user, bestPlace3, vote);
        SelectPlace selectPlace3 = saveSelectPlace(admin, bestPlace1, vote);
        SelectPlace selectPlace4 = saveSelectPlace(admin, bestPlace2, vote);

        em.flush();
        em.clear();

        // when // then
        assertThatThrownBy(() -> voteService.conclusionVote(group.getSpaceId(), user))
                .extracting("result.code", "result.message")
                .contains(-1005, "해당 유저는 그룹의 어드민이 아닙니다.");
    }

    @DisplayName("모임장이 재투표를 진행한다.")
    @Test
    void reCreateVote() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");

        Space group = savedGroup(admin.getUserId(), "테스트 그룹");

        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);

        Vote vote = saveVote(group.getSpaceId(), true, true, LocalDateTime.of(2023, 8, 3, 12, 0, 0));

        SelectPlace selectPlace1 = saveSelectPlace(user, bestPlace1, vote);
        SelectPlace selectPlace2 = saveSelectPlace(user, bestPlace3, vote);
        SelectPlace selectPlace3 = saveSelectPlace(admin, bestPlace1, vote);
        SelectPlace selectPlace4 = saveSelectPlace(admin, bestPlace2, vote);

        VoteCreateRequest request = VoteCreateRequest.toRequest(false, true, null);

        // when
        VoteCreateResponse response = voteService.reCreateVote(request, group.getSpaceId(), admin);

        // then
        assertThat(response)
                .extracting("groupId", "isClosed", "isAnonymous", "isEnabledMultipleChoice", "endAt")
                .contains(group.getSpaceId(), false, true, true, "none");
    }

    // method
    private Vote saveVote(
            Long groupId, Boolean isAnonymous, boolean isEnabledMultipleChoice, LocalDateTime endAt
    ) {
        return voteRepository.save(
                Vote.builder()
                        .spaceId(groupId)
                        .isClosed(false)
                        .isAnonymous(isAnonymous)
                        .isEnabledMultipleChoice(isEnabledMultipleChoice)
                        .endAt(endAt)
                        .build()
        );
    }

    private SelectPlace saveSelectPlace(Users user, BestPlace bestPlace, Vote vote) {
        return selectPlaceRepository.save(
                SelectPlace.builder()
                        .vote(vote)
                        .userId(user.getUserId())
                        .bestPlace(bestPlace)
                        .build()
        );
    }

    private BestPlace saveBestPlace(Space group, String placeName, double longitude, double latitude) {
        return bestPlaceRepository.save(
                BestPlace.builder()
                        .space(group)
                        .placeName(placeName)
                        .longitude(longitude)
                        .latitude(latitude)
                        .build()
        );
    }

    private Space savedGroup(Long userId, String name) {
        return groupRepository.save(
                Space.builder()
                        .adminId(userId)
                        .name(name)
                        .place("none")
                        .date(LocalDate.of(2023, 7, 10))
                        .build()
        );
    }

    private Participation savedParticipation(
            Users user, Space group, String userName,
            String locationName, Double latitude, Double longitude,
            String type
    ) {
        return participationRepository.save(
                Participation.builder()
                        .space(group)
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