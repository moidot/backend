package com.moim.backend.domain.groupvote.repository;

import com.moim.backend.TestQueryDSLConfig;
import com.moim.backend.domain.groupvote.entity.SelectPlace;
import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
import com.moim.backend.domain.space.repository.BestPlaceRepository;
import com.moim.backend.domain.space.repository.GroupRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestQueryDSLConfig.class)
@Transactional
class SelectPlaceRepositoryTest {

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

    @DisplayName("추천장소와 투표내용을 fetchJoin 으로 한꺼번에 가져온다.")
    @Test
    void findByVoteStatus() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");
        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);
        Vote vote = saveVote(group.getGroupId(), true, false, null);
        SelectPlace selectPlace1 = saveSelectPlace(admin, bestPlace1, vote);

        em.flush();
        em.clear();

        // when
        List<BestPlace> bestPlaces = selectPlaceRepository.findByVoteStatus(vote.getVoteId());

        // then
        assertThat(bestPlaces)
                .extracting("bestPlaceId")
                .contains(
                        bestPlace1.getBestPlaceId(),
                        bestPlace2.getBestPlaceId(),
                        bestPlace3.getBestPlaceId()
                );

        assertThat(bestPlaces.get(0).getSelectPlaces())
                .extracting("selectPlaceId")
                .contains(
                        selectPlace1.getSelectPlaceId()
                );
    }

    @DisplayName("현재 유저가 참가중인 투표를 불러온다.")
    @Test
    void findSelectPlaceByUserId() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");
        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);
        Vote vote = saveVote(group.getGroupId(), true, false, null);
        SelectPlace selectPlace1 = saveSelectPlace(admin, bestPlace1, vote);
        SelectPlace selectPlace2 = saveSelectPlace(admin, bestPlace2, vote);
        SelectPlace selectPlace3 = saveSelectPlace(admin, bestPlace3, vote);

        // when
        List<Long> optionalSelectPlaceIds =
                selectPlaceRepository.findSelectPlaceByUserIdAndVoteId(admin.getUserId(), vote.getVoteId());

        // then

        assertThat(optionalSelectPlaceIds)
                .contains(
                        selectPlace1.getSelectPlaceId(), selectPlace2.getSelectPlaceId(),
                        selectPlace3.getSelectPlaceId()
                );
    }

    @DisplayName("현재 유저가 참가중인 투표를 불러올때, 아직 한번도 투표를 하지 않아 데이터가 없다.")
    @Test
    void findSelectPlaceByUserIdWithNullData() {
        // given
        Users user = savedUser("test@test.com", "테스트");
        Users admin = savedUser("admin@admin.com", "어드민");
        Groups group = savedGroup(admin.getUserId(), "테스트 그룹");
        BestPlace bestPlace1 = saveBestPlace(group, "강남역", 123.123456, 123.123456);
        BestPlace bestPlace2 = saveBestPlace(group, "역삼역", 123.123456, 123.123456);
        BestPlace bestPlace3 = saveBestPlace(group, "신논현역", 123.123456, 123.123456);
        Vote vote = saveVote(group.getGroupId(), true, false, null);

        // when
        List<Long> optionalSelectPlaceIds =
                selectPlaceRepository.findSelectPlaceByUserIdAndVoteId(user.getUserId(), vote.getVoteId());

        // then

        assertThat(optionalSelectPlaceIds.isEmpty()).isTrue();

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