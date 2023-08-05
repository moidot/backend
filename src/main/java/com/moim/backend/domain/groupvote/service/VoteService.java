package com.moim.backend.domain.groupvote.service;

import com.moim.backend.domain.groupvote.entity.SelectPlace;
import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.groupvote.repository.SelectPlaceRepository;
import com.moim.backend.domain.groupvote.repository.VoteRepository;
import com.moim.backend.domain.groupvote.request.VoteServiceRequest;
import com.moim.backend.domain.groupvote.response.VoteResponse;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.repository.BestPlaceRepository;
import com.moim.backend.domain.space.repository.GroupRepository;
import com.moim.backend.domain.space.repository.ParticipationRepository;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.common.Result;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.moim.backend.global.common.Result.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {

    private final ParticipationRepository participationRepository;
    private final GroupRepository groupRepository;
    private final VoteRepository voteRepository;
    private final BestPlaceRepository bestPlaceRepository;
    private final SelectPlaceRepository selectPlaceRepository;

    @Transactional
    public VoteResponse.Create createVote(VoteServiceRequest.Create request, Long groupId, Users user) {
        Groups group = getGroups(groupId);

        if (!group.getAdminId().equals(user.getUserId())) {
            throw new CustomException(NOT_ADMIN_USER);
        }

        Vote vote = voteRepository.save(
                Vote.builder()
                        .groupId(groupId)
                        .isAnonymous(request.getIsAnonymous())
                        .isEnabledMultipleChoice(request.getIsEnabledMultipleChoice())
                        .isClosed(false)
                        .endAt(request.getEndAt().orElse(null))
                        .build()
        );

        return VoteResponse.Create.response(vote);
    }

    @Transactional
    public VoteResponse.SelectResult selectVote(Long groupId, List<Long> bestPlaceIds, Users user, LocalDateTime now) {
        // 투표 개설 및 투표에 대한 유효성 검증
        Groups group = getGroups(groupId);
        Vote vote = getVote(groupId);
        validateVote(bestPlaceIds, now, vote);

        // 이미 투표를 했다면, 현재 유저가 투표한 목록을 가져온 후 제거
        List<Long> selectPlaceIds =
                selectPlaceRepository.findSelectPlaceByUserIdAndVoteId(user.getUserId(), vote.getVoteId());
        if (!bestPlaceIds.isEmpty()) {
            selectPlaceRepository.deleteAllById(selectPlaceIds);
        }

        // 요청받은 bestPlaceId 값을 이용해 for 문을 돌면서 save
        List<BestPlace> BestPlaces = bestPlaceRepository.findAllById(bestPlaceIds);
        for (BestPlace bestPlace : BestPlaces) {
            selectPlaceRepository.save(
                    SelectPlace.builder()
                            .bestPlace(bestPlace)
                            .vote(vote)
                            .userId(user.getUserId())
                            .build()
            );
        }

        // 투표 이후 현재 추천된 장소들의 현황을 조회
        List<BestPlace> bestPlaces = selectPlaceRepository.findByVoteStatus(vote.getGroupId());
        List<VoteResponse.VoteStatus> voteStatuses = getVoteStatuses(user, bestPlaces);
        return VoteResponse.SelectResult.response(group, vote, voteStatuses);
    }

    public VoteResponse.SelectResult readVote(Long groupId, Users user) {
        Groups group = getGroups(groupId);

        // TODO: 어떤 방식이 프론트엔드에서 처리하기 편한지 확인해봐야 할 것 같음
        // 투표가 개설되지 않은 상태면 Exception 발생
        Vote vote = getVote(groupId);

        // 투표 이후 현재 추천된 장소들의 현황을 조회
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

    private Groups getGroups(Long groupId) {
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
