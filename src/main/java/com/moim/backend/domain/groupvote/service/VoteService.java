package com.moim.backend.domain.groupvote.service;

import com.moim.backend.domain.groupvote.entity.SelectPlace;
import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.groupvote.repository.SelectPlaceRepository;
import com.moim.backend.domain.groupvote.repository.VoteRepository;
import com.moim.backend.domain.groupvote.request.service.VoteCreateServiceRequest;
import com.moim.backend.domain.groupvote.response.VoteCreateResponse;
import com.moim.backend.domain.groupvote.response.VoteResponse;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.repository.BestPlaceRepository;
import com.moim.backend.domain.space.repository.GroupRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.common.exception.CustomException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.moim.backend.global.common.Result.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {

    private final ParticipationRepository participationRepository;
    private final GroupRepository groupRepository;
    private final VoteRepository voteRepository;
    private final BestPlaceRepository bestPlaceRepository;
    private final SelectPlaceRepository selectPlaceRepository;

    // 투표 생성 API
    @Transactional
    public VoteCreateResponse createVote(VoteCreateServiceRequest request, Long groupId, Users user) {
        Groups group = getGroup(groupId);
        validateUserIsAdmin(user, group);

        return VoteCreateResponse.response(
                voteRepository.save(toVoteEntity(request, groupId))
        );
    }

    private static void validateUserIsAdmin(Users user, Groups group) {
        if (!group.getAdminId().equals(user.getUserId())) {
            throw new CustomException(NOT_ADMIN_USER);
        }
    }

    private static Vote toVoteEntity(VoteCreateServiceRequest request, Long groupId) {
        return Vote.builder()
                .groupId(groupId)
                .isAnonymous(request.getIsAnonymous())
                .isEnabledMultipleChoice(request.getIsEnabledMultipleChoice())
                .isClosed(false)
                .endAt(request.getEndAt().orElse(null))
                .build();
    }

    // 투표 참여 API
    @Transactional
    public VoteResponse.SelectResult selectVote(
            Long groupId, List<Long> selectPlaceIds, Users user, LocalDateTime now
    ) {
        Groups group = getGroup(groupId);
        Vote vote = getVote(groupId);
        validateVote(selectPlaceIds, now, vote);
        try {
            processUserVotes(selectPlaceIds, user, vote);
            return VoteResponse.SelectResult.response(group, vote, toVoteStatusResponse(user, vote));
        } catch (OptimisticLockException ole) {
            throw new CustomException(CONCURRENCY_ISSUE_DETECTED);
        }
    }

    private void processUserVotes(List<Long> selectPlaceIds, Users user, Vote vote) {
        removeUserVotesIfExist(selectPlaceIds, user, vote);
        saveUserVotesForSelectPlaces(selectPlaceIds, user, vote);
    }

    private List<VoteResponse.VoteStatus> toVoteStatusResponse(Users user, Vote vote) {
        List<BestPlace> bestPlaces = selectPlaceRepository.findByVoteStatus(vote.getGroupId());
        return getVoteStatuses(user, bestPlaces);
    }

    private void saveUserVotesForSelectPlaces(List<Long> selectPlaceIds, Users user, Vote vote) {
        List<BestPlace> selectPlaces = bestPlaceRepository.findAllById(selectPlaceIds);
        for (BestPlace selectPlace : selectPlaces) {
            selectPlaceRepository.save(
                    SelectPlace.builder()
                            .bestPlace(selectPlace)
                            .vote(vote)
                            .userId(user.getUserId())
                            .build()
            );
        }
    }

    private void removeUserVotesIfExist(List<Long> bestPlaceIds, Users user, Vote vote) {
        List<Long> selectPlaceIds =
                selectPlaceRepository.findSelectPlaceByUserIdAndVoteId(user.getUserId(), vote.getVoteId());
        if (!bestPlaceIds.isEmpty()) {
            selectPlaceRepository.deleteAllById(selectPlaceIds);
        }
    }

    // 투표 읽기 API
    public VoteResponse.SelectResult readVote(Long groupId, Users user) {
        Groups group = getGroup(groupId);
        Optional<Vote> optionalVote = voteRepository.findByGroupId(groupId);
        if (optionalVote.isEmpty()) {
            return VoteResponse.SelectResult.response(group, null, new ArrayList<>());
        } else {
            // 투표 이후 현재 추천된 장소들의 현황을 조회
            Vote vote = optionalVote.get();
            List<BestPlace> bestPlaces = selectPlaceRepository.findByVoteStatus(vote.getGroupId());
            List<VoteResponse.VoteStatus> voteStatuses = getVoteStatuses(user, bestPlaces);
            return VoteResponse.SelectResult.response(group, vote, voteStatuses);
        }
    }

    // 해당 장소 투표한 인원 리스트 조회하기 API
    public List<VoteResponse.SelectPlaceUser> readSelectPlaceUsers(Long groupId, Long bestPlaceId, Users user) {
        List<SelectPlace> selectPlaceList = getSelectPlaceList(bestPlaceId);

        List<Long> userIds = extractUserIdsFromSelectPlaces(selectPlaceList);
        List<Participation> participations = participationRepository.findAllByGroupGroupIdAndUserIdIn(groupId, userIds);

        Groups group = getGroup(groupId);
        return participations.stream().map(participation -> VoteResponse.SelectPlaceUser.response(
                participation, isAdmin(group, participation))
        ).toList();
    }

    private static Boolean isAdmin(Groups group, Participation participation) {
        return (participation.getUserId().equals(group.getAdminId())) ? TRUE : FALSE;
    }

    private static List<Long> extractUserIdsFromSelectPlaces(List<SelectPlace> selectPlaceList) {
        List<Long> userIds = new ArrayList<>();
        for (SelectPlace selectPlace : selectPlaceList) {
            userIds.add(selectPlace.getUserId());
        }
        return userIds;
    }

    private List<SelectPlace> getSelectPlaceList(Long bestPlaceId) {
        BestPlace bestPlace = bestPlaceRepository.findById(bestPlaceId).orElseThrow(
                () -> new CustomException(NOT_FOUND_BESTPLACE)
        );

        if (bestPlace.getSelectPlaces().isEmpty()) {
            throw new CustomException(NOT_VOTED_PLACE);
        }

        return bestPlace.getSelectPlaces();
    }

    // 투표 종료하기 API
    @Transactional
    public VoteResponse.SelectResult conclusionVote(Long groupId, Users user) {
        Groups group = getGroup(groupId);
        validateUserIsAdmin(user, group);
        Vote vote = getVote(groupId);

        // 투표 종료
        vote.conclusionVote();

        // 가장 높은 투표를 받은 장소 선정
        BestPlace confirmPlace = group.getBestPlaces().stream()
                .max(Comparator.comparing(bestPlace -> bestPlace.getSelectPlaces().size()))
                .orElseThrow(() -> new CustomException(FAIL));
        group.confirmPlace(confirmPlace.getPlaceName());

        // 종료 이후 현재 추천된 장소들의 현황을 조회
        List<BestPlace> bestPlaces = selectPlaceRepository.findByVoteStatus(vote.getGroupId());
        List<VoteResponse.VoteStatus> voteStatuses = getVoteStatuses(user, bestPlaces);

        return VoteResponse.SelectResult.response(group, vote, voteStatuses);
    }

    // method

    private Vote getVote(Long groupId) {
        return voteRepository.findByGroupId(groupId)
                .orElseThrow(
                        () -> new CustomException(NOT_CREATED_VOTE)
                );
    }

    private Groups getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(
                        () -> new CustomException(NOT_FOUND_GROUP)
                );
    }

    private static void validateVote(List<Long> bestPlaceIds, LocalDateTime now, Vote vote) {
        if (vote.getIsClosed()) {
            throw new CustomException(IS_CLOSED_VOTE);
        }
        if (!vote.getIsEnabledMultipleChoice() && bestPlaceIds.size() > 1) {
            throw new CustomException(NOT_MULTIPLE_CHOICE);
        }
        Optional<LocalDateTime> endAt = Optional.ofNullable(vote.getEndAt());
        if (endAt.isPresent() && endAt.get().isBefore(now)) {
            throw new CustomException(VOTE_ALREADY_ENDED);
        }
    }

    private static List<VoteResponse.VoteStatus> getVoteStatuses(Users user, List<BestPlace> bestPlaces) {
        return bestPlaces.stream().map(bestPlace -> {
            // 내가 투표했는지 여부 확인
            Boolean isVoted = bestPlace.getSelectPlaces().stream()
                    .anyMatch(selectPlace -> selectPlace.getUserId().equals(user.getUserId()));

            return VoteResponse.VoteStatus.toStatusDto(bestPlace, isVoted);
        }).toList();
    }
}
