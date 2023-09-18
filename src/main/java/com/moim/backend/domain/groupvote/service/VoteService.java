package com.moim.backend.domain.groupvote.service;

import com.moim.backend.domain.groupvote.entity.SelectPlace;
import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.groupvote.repository.SelectPlaceRepository;
import com.moim.backend.domain.groupvote.repository.VoteRepository;
import com.moim.backend.domain.groupvote.request.VoteServiceRequest;
import com.moim.backend.domain.groupvote.response.VoteResponse;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.moim.backend.global.common.Result.*;
import static java.lang.Boolean.*;

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

    // 투표 참여 API
    @Transactional
    public VoteResponse.SelectResult selectVote(
            Long groupId, List<Long> selectPlaceIds, Users user, LocalDateTime now
    ) {
        Groups group = getGroups(groupId);
        Vote vote = getVote(groupId);
        validateVote(selectPlaceIds, now, vote);

        // 이미 투표를 했다면, 현재 유저가 투표한 목록을 가져온 후 제거
        removeUserVotesIfExist(selectPlaceIds, user, vote);

        // 요청받은 bestPlaceId 값을 이용해 for 문을 돌면서 투표 save
        saveUserVotesForSelectPlaces(selectPlaceIds, user, vote);

        return VoteResponse.SelectResult.response(group, vote, toVoteStatusResponse(user, vote));
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
        Groups group = getGroups(groupId);
        Vote vote = getVote(groupId);

        // 투표 이후 현재 추천된 장소들의 현황을 조회
        List<BestPlace> bestPlaces = selectPlaceRepository.findByVoteStatus(vote.getGroupId());
        List<VoteResponse.VoteStatus> voteStatuses = getVoteStatuses(user, bestPlaces);
        return VoteResponse.SelectResult.response(group, vote, voteStatuses);
    }

    // 해당 장소 투표한 인원 리스트 조회하기 API
    public List<VoteResponse.SelectPlaceUser> readSelectPlaceUsers(Long groupId, Long bestPlaceId, Users user) {
        // 그룹의 추천장소와 투표 리스트를 fetch 조회
        Groups group = groupRepository.findByIdToFetchJoinBestPlace(groupId)
                .orElseThrow(
                        () -> new CustomException(NOT_FOUND_GROUP)
                );
        BestPlace bestPlace = filteredBestPlace(bestPlaceId, group);

        // 선택된 BestPlace 에 투표한 유저Id로 그룹 참여자 조회
        if (bestPlace.getSelectPlaces().isEmpty()) {
            throw new CustomException(NOT_VOTED_PLACE);
        }
        List<Long> userIds = new ArrayList<>();
        for (SelectPlace selectPlace : bestPlace.getSelectPlaces()) {
            userIds.add(selectPlace.getUserId());
        }
        List<Participation> participations = participationRepository.findAllByGroupAndUserIdIn(group, userIds);

        // 어드민 판별 후 DTO 변환
        return participations.stream().map(
                participation -> {
                    Boolean isAdmin = (participation.getUserId().equals(group.getAdminId())) ? TRUE : FALSE;
                    return VoteResponse.SelectPlaceUser.response(participation, isAdmin);
                }).toList();
    }

    // 투표 종료하기 API
    @Transactional
    public VoteResponse.SelectResult conclusionVote(Long groupId, Users user) {
        Groups group = getGroups(groupId);
        if (!group.getAdminId().equals(user.getUserId())) {
            throw new CustomException(NOT_ADMIN_USER);
        }
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

    private static BestPlace filteredBestPlace(Long bestPlaceId, Groups group) {
        return group.getBestPlaces().stream()
                .filter(groups -> groups.getBestPlaceId().equals(bestPlaceId))
                .findFirst()
                .orElseThrow(() -> new CustomException(NOT_FOUND_BESTPLACE));
    }

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
