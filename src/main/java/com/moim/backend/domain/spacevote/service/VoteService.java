package com.moim.backend.domain.spacevote.service;

import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Participation;
import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.repository.BestPlaceRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.space.repository.SpaceRepository;
import com.moim.backend.domain.spacevote.entity.SelectPlace;
import com.moim.backend.domain.spacevote.entity.Vote;
import com.moim.backend.domain.spacevote.repository.SelectPlaceRepository;
import com.moim.backend.domain.spacevote.repository.VoteRepository;
import com.moim.backend.domain.spacevote.request.controller.VoteCreateRequest;
import com.moim.backend.domain.spacevote.response.VoteCreateResponse;
import com.moim.backend.domain.spacevote.response.VoteParticipation;
import com.moim.backend.domain.spacevote.response.VoteSelectPlaceUserResponse;
import com.moim.backend.domain.spacevote.response.VoteSelectResultResponse;
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
    private final SpaceRepository groupRepository;
    private final VoteRepository voteRepository;
    private final BestPlaceRepository bestPlaceRepository;
    private final SelectPlaceRepository selectPlaceRepository;

    // 투표 생성 API
    @Transactional
    public VoteCreateResponse createVote(VoteCreateRequest request, Long groupId, Users user) {
        Space group = getSpace(groupId);
        validateAlreadyCreatedVote(groupId);
        validateUserIsAdmin(user, group);
        validateOnlyAdminCreatedVote(group);
        Vote vote = voteRepository.save(toVoteEntity(request, groupId));

        return VoteCreateResponse.response(vote);
    }

    private static void validateOnlyAdminCreatedVote(Space group) {
        if (group.getParticipations().size() == 1) {
            throw new CustomException(NOT_CREATED_VOTE);
        }
    }

    private void validateAlreadyCreatedVote(Long groupId) {
        if (voteRepository.findBySpaceId(groupId).isPresent()) {
            throw new CustomException(ALREADY_CREATED_VOTE);
        }
    }

    private static void validateUserIsAdmin(Users user, Space group) {
        if (!group.getAdminId().equals(user.getUserId())) {
            throw new CustomException(NOT_ADMIN_USER);
        }
    }

    private static Vote toVoteEntity(VoteCreateRequest request, Long groupId) {
        return Vote.builder()
                .spaceId(groupId)
                .isAnonymous(request.getIsAnonymous())
                .isEnabledMultipleChoice(request.getIsEnabledMultipleChoice())
                .isClosed(false)
                .endAt(request.getEndAt())
                .build();
    }

    // 투표 참여 API
    @Transactional
    public VoteSelectResultResponse selectVote(
            Long groupId, List<Long> selectPlaceIds, Users user, LocalDateTime now
    ) {
        Space group = getSpace(groupId);
        Vote vote = getVote(groupId);
        validateVote(selectPlaceIds, now, vote);
        try {
            processUserVotes(selectPlaceIds, user, vote);
            return VoteSelectResultResponse.response(
                    group, vote, toVoteStatusResponse(user, vote), selectPlaceRepository.countByVote(vote), true
            );
        } catch (OptimisticLockException ole) {
            throw new CustomException(CONCURRENCY_ISSUE_DETECTED);
        }
    }

    private void processUserVotes(List<Long> selectPlaceIds, Users user, Vote vote) {
        removeUserVotesIfExist(selectPlaceIds, user, vote);
        saveUserVotesForSelectPlaces(selectPlaceIds, user, vote);
    }

    private List<VoteSelectResultResponse.VoteStatus> toVoteStatusResponse(Users user, Vote vote) {
        List<BestPlace> bestPlaces = selectPlaceRepository.findByVoteStatus(vote.getSpaceId());
        return getVoteStatuses(user.getUserId(), bestPlaces);
    }

    private void saveUserVotesForSelectPlaces(List<Long> selectPlaceIds, Users user, Vote vote) {
        List<BestPlace> bestPlaces = bestPlaceRepository.findAllById(selectPlaceIds);
        for (BestPlace bestPlace : bestPlaces) {
            selectPlaceRepository.save(
                    SelectPlace.builder()
                            .bestPlace(bestPlace)
                            .vote(vote)
                            .userId(user.getUserId())
                            .build()
            );
        }
    }

    private void removeUserVotesIfExist(List<Long> bestPlaceIds, Users user, Vote vote) {
        List<Long> selectPlaceIds =
                selectPlaceRepository.findSelectPlaceByUserIdAndVoteId(user.getUserId(), vote.getVoteId());
        selectPlaceRepository.deleteAllById(selectPlaceIds);
    }

    // 투표 읽기 API
    public VoteSelectResultResponse readVote(Long groupId, Long userId) {
        Space group = getSpace(groupId);
        Optional<Vote> optionalVote = voteRepository.findBySpaceId(groupId);
        if (optionalVote.isEmpty()) {
            return VoteSelectResultResponse.response(group, null, new ArrayList<>(), 0, false);
        }
        Vote vote = optionalVote.get();
        Boolean isVotingParticipant = selectPlaceRepository.existsByVoteAndUserId(vote, userId);
        // 투표 이후 현재 추천된 장소들의 현황을 조회
        List<BestPlace> bestPlaces = selectPlaceRepository.findByVoteStatus(vote.getSpaceId());
        List<VoteSelectResultResponse.VoteStatus> voteStatuses = getVoteStatuses(userId, bestPlaces);
        return VoteSelectResultResponse.response(group, vote, voteStatuses, selectPlaceRepository.countByVote(vote), isVotingParticipant);
    }

    // 해당 장소 투표한 인원 리스트 조회하기 API
    public VoteSelectPlaceUserResponse readSelectPlaceUsers(Long groupId, Long bestPlaceId) {
        List<SelectPlace> selectPlaceList = getSelectPlaceList(bestPlaceId);

        List<Long> userIds = extractUserIdsFromSelectPlaces(selectPlaceList);
        List<Participation> participations = participationRepository.findAllBySpaceSpaceIdAndUserIdIn(groupId, userIds);

        Space group = getSpace(groupId);
        Vote vote = getVote(groupId);

        List<VoteParticipation> voteParticipations = participations.stream().map(
                participation -> VoteParticipation.response(participation, isAdmin(group, participation))
        ).toList();
        return VoteSelectPlaceUserResponse.response(selectPlaceRepository.countByVote(vote), voteParticipations);
    }

    private static Boolean isAdmin(Space group, Participation participation) {
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

        return bestPlace.getSelectPlaces();
    }

    // 투표 종료하기 API
    @Transactional
    public VoteSelectResultResponse conclusionVote(Long groupId, Users user) {
        Space group = getSpace(groupId);
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
        List<BestPlace> bestPlaces = selectPlaceRepository.findByVoteStatus(vote.getSpaceId());
        List<VoteSelectResultResponse.VoteStatus> voteStatuses = getVoteStatuses(user.getUserId(), bestPlaces);

        return VoteSelectResultResponse.response(group, vote, voteStatuses, selectPlaceRepository.countByVote(vote), true);
    }

    // 재투표 API
    @Transactional
    public VoteCreateResponse reCreateVote(VoteCreateRequest request, Long spaceId, Users user) {
        Space space = getSpace(spaceId);
        validateUserIsAdmin(user, space);

        Vote vote = getVote(spaceId);
        deleteVote(vote);

        Vote newVote = voteRepository.save(
                Vote.builder()
                        .spaceId(spaceId)
                        .isAnonymous(request.getIsAnonymous())
                        .isEnabledMultipleChoice(request.getIsEnabledMultipleChoice())
                        .isClosed(false)
                        .endAt(request.getEndAt())
                        .build()
        );

        return VoteCreateResponse.response(newVote);
    }

    private void deleteVote(Vote vote) {
        selectPlaceRepository.deleteByVote(vote);
        voteRepository.delete(vote);
    }

    // method

    private Vote getVote(Long groupId) {
        return voteRepository.findBySpaceId(groupId)
                .orElseThrow(
                        () -> new CustomException(NOT_CREATED_VOTE)
                );
    }

    private Space getSpace(Long groupId) {
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

    private static List<VoteSelectResultResponse.VoteStatus> getVoteStatuses(Long userId, List<BestPlace> bestPlaces) {
        return bestPlaces.stream().map(bestPlace -> {
            // 유저가 존재하는 경우 투표 여부 확인
            if (userId != -1) {
                Boolean isVoted = bestPlace.getSelectPlaces().stream()
                        .anyMatch(selectPlace -> selectPlace.getUserId().equals(userId));
                return VoteSelectResultResponse.VoteStatus.toStatusDto(bestPlace, isVoted);
            }
            return VoteSelectResultResponse.VoteStatus.toStatusDto(bestPlace, false);
        }).toList();
    }
}
