package com.moim.backend.domain.space.repository;

import com.moim.backend.TestQueryDSLConfig;
import com.moim.backend.domain.groupvote.entity.SelectPlace;
import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.groupvote.repository.SelectPlaceRepository;
import com.moim.backend.domain.groupvote.repository.VoteRepository;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.TransportationType;
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

@DataJpaTest
@ActiveProfiles("test")
@Import(TestQueryDSLConfig.class)
@Transactional
class GroupRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private SelectPlaceRepository selectPlaceRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private BestPlaceRepository bestPlaceRepository;

    @DisplayName("유저가 참여하고있는 모임의 정보를 fetchJoin 으로 데이터를 가져온다.")
    @Test
    void findByGroupsByFetch() {
        // given
        Users admin1 = savedUser("admin1@test.com", "어드민1");
        Users admin2 = savedUser("admin2@test.com", "어드민2");
        Users admin3 = savedUser("admin3@test.com", "어드민3");

        Groups group1 = savedGroup(admin1.getUserId(), "그룹1");
        Groups group2 = savedGroup(admin2.getUserId(), "그룹2");
        Groups group3 = savedGroup(admin3.getUserId(), "그룹3");

        saveBestPlace(group1, "의정부역", 127.123456, 36.123456);
        saveBestPlace(group1, "서울역", 127.123457, 36.123457);
        saveBestPlace(group1, "개봉역", 127.123458, 36.123458);

        Users user1 = savedUser("test1@test.com", "테스트1");
        Users user2 = savedUser("test2@test.com", "테스트2");

        savedParticipation(admin1, group1, "어드민", "아무데나", 36.23423, 127.32423, "PUBLIC");
        savedParticipation(admin2, group2, "어드민", "아무데나", 36.23423, 127.32423, "PUBLIC");
        savedParticipation(admin3, group3, "어드민", "아무데나", 36.23423, 127.32423, "PUBLIC");

        savedParticipation(user1, group1, "양쿵", "아무데나", 36.23423, 127.32423, "PUBLIC");
        savedParticipation(user1, group2, "양쿵", "아무데나", 36.23423, 127.32423, "PUBLIC");
        savedParticipation(user1, group3, "양쿵", "아무데나", 36.23423, 127.32423, "PUBLIC");

        savedParticipation(user2, group1, "양쿵", "아무데나", 36.23423, 127.32423, "PUBLIC");
        savedParticipation(user2, group3, "양쿵", "아무데나", 36.23423, 127.32423, "PUBLIC");

        em.flush();
        em.clear();

        // when
        List<Groups> groups = groupRepository.findByGroupsByFetch(user1.getUserId());

        // then
        assertThat(groups).hasSize(3)
                .extracting("groupId", "name")
                .contains(
                        tuple(group1.getGroupId(), "그룹1"),
                        tuple(group2.getGroupId(), "그룹2"),
                        tuple(group3.getGroupId(), "그룹3")
                );

        assertThat(groups.get(0))
                .extracting("groupId", "name", "date", "place")
                .contains(group1.getGroupId(), "그룹1", LocalDate.of(2023, 7, 10), "none");

        assertThat(groups.get(0).getBestPlaces())
                .extracting("placeName", "longitude", "latitude")
                .contains(
                        tuple("의정부역", 127.123456, 36.123456),
                        tuple("서울역", 127.123457, 36.123457),
                        tuple("개봉역", 127.123458, 36.123458)
                );
    }

    @DisplayName("그룹을 가져올때 추천장소까지 fetchJoin 으로 가져온다.")
    @Test
    void findByIdToFetchJoin() {
        // given
        Users admin1 = savedUser("admin1@test.com", "어드민1");
        Groups group1 = savedGroup(admin1.getUserId(), "그룹1");

        BestPlace bestPlace1 = saveBestPlace(group1, "의정부역", 127.123456, 36.123456);
        BestPlace bestPlace2 = saveBestPlace(group1, "서울역", 127.123457, 36.123457);
        BestPlace bestPlace3 = saveBestPlace(group1, "개봉역", 127.123458, 36.123458);

        Users user1 = savedUser("test1@test.com", "테스트1");
        Users user2 = savedUser("test2@test.com", "테스트2");

        savedParticipation(admin1, group1, "어드민", "아무데나", 36.23423, 127.32423, "PUBLIC");
        savedParticipation(user1, group1, "양쿵", "아무데나", 36.23423, 127.32423, "PUBLIC");
        savedParticipation(user2, group1, "양쿵", "아무데나", 36.23423, 127.32423, "PUBLIC");

        Vote vote = voteRepository.save(
                Vote.builder()
                        .groupId(group1.getGroupId())
                        .isClosed(false)
                        .isAnonymous(true)
                        .isEnabledMultipleChoice(true)
                        .build()
        );

        selectPlaceRepository.save(
                SelectPlace.builder()
                        .userId(admin1.getUserId())
                        .vote(vote)
                        .bestPlace(bestPlace1)
                        .build()
        );

        selectPlaceRepository.save(
                SelectPlace.builder()
                        .userId(admin1.getUserId())
                        .vote(vote)
                        .bestPlace(bestPlace3)
                        .build()
        );

        em.flush();
        em.clear();

        // when
        Optional<Groups> optionalGroup = groupRepository.findByIdToFetchJoinBestPlace(group1.getGroupId());

        // then
        Groups group = optionalGroup.get();
        assertThat(group.getBestPlaces())
                .extracting("placeName", "longitude", "latitude")
                .contains(
                        tuple("의정부역", 127.123456, 36.123456),
                        tuple("서울역", 127.123457, 36.123457),
                        tuple("개봉역", 127.123458, 36.123458)
                );

        for (BestPlace bestPlace : group.getBestPlaces()) {
            for (SelectPlace selectPlace : bestPlace.getSelectPlaces()) {
                System.out.println(selectPlace.getUserId());
            }
        }
    }

    @DisplayName("")
    @Test
    void findByGroupParticipation() {
        // given
        Users user1 = savedUser("test1@test.com", "모이닷 운영자1");
        Users user2 = savedUser("test2@test.com", "모이닷 운영자2");
        Users user3 = savedUser("test3@test.com", "모이닷 운영자3");
        Users user4 = savedUser("test4@test.com", "모이닷 운영자4");

        Groups group = savedGroup(user1.getUserId(), "모이닷");

        Participation participation1 = savedParticipation(user1, group, "모이닷1", "서울 성북구 보문로34다길 2", 36.123456, 127.1234567, "PERSONAL");
        Participation participation2 = savedParticipation(user2, group, "모이닷2", "서울 강북구 도봉로 76가길 55", 36.123456, 127.1234567, "PUBLIC");
        Participation participation3 = savedParticipation(user3, group, "모이닷3", "서울 강북구 도봉로 76가길 54", 36.123456, 127.1234567, "PERSONAL");
        Participation participation4 = savedParticipation(user4, group, "모이닷4", "경기도 부천시 부천로 1", 36.123456, 127.1234567, "PUBLIC");

        em.flush();
        em.clear();

        // when
        Groups validateGroup = groupRepository.findByGroupParticipation(group.getGroupId()).get();

        // then
        assertThat(validateGroup.getParticipations())
                .extracting("userName", "locationName")
                .contains(
                        tuple("모이닷1", "서울 성북구 보문로34다길 2"),
                        tuple("모이닷2", "서울 강북구 도봉로 76가길 55"),
                        tuple("모이닷3", "서울 강북구 도봉로 76가길 54"),
                        tuple("모이닷4", "경기도 부천시 부천로 1"));
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

    private Users savedUser(String email, String name) {
        return userRepository.save(
                Users.builder()
                        .email(email)
                        .name(name)
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
}