package com.moim.backend.domain.groupvote.response;

import com.moim.backend.domain.groupvote.entity.Vote;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Groups;
import com.moim.backend.domain.space.entity.Participation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class VoteResponse {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Create {
        private Long voteId;
        private Long groupId;
        private Boolean isClosed;
        private Boolean isAnonymous;
        private Boolean isEnabledMultipleChoice;
        private String endAt;

        public static VoteResponse.Create response(Vote vote) {
            String endAt = Optional.ofNullable(vote.getEndAt())
                    .map(time -> time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .orElse("none");

            return Create.builder()
                    .voteId(vote.getVoteId())
                    .groupId(vote.getGroupId())
                    .isClosed(vote.getIsClosed())
                    .isAnonymous(vote.getIsAnonymous())
                    .isEnabledMultipleChoice(vote.getIsEnabledMultipleChoice())
                    .endAt(endAt)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class SelectResult {
        private Long groupId;
        private String groupName;
        private String groupDate;
        private Long voteId;
        private Boolean isClosed;
        private Boolean isAnonymous;
        private Boolean isEnabledMultipleChoice;
        private String endAt;
        private List<VoteStatus> voteStatuses;

        public static VoteResponse.SelectResult response(
                Groups group, Vote vote, List<VoteStatus> voteStatuses
        ) {

            String groupDate = Optional.ofNullable(group.getDate())
                    .map(date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .orElse("none");

            String endAt = Optional.ofNullable(vote.getEndAt())
                    .map(time -> time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .orElse("none");

            return SelectResult.builder()
                    .groupId(group.getGroupId())
                    .groupName(group.getName())
                    .groupDate(groupDate)
                    .voteId(vote.getVoteId())
                    .isClosed(vote.getIsClosed())
                    .isAnonymous(vote.getIsAnonymous())
                    .isEnabledMultipleChoice(vote.getIsEnabledMultipleChoice())
                    .endAt(endAt)
                    .voteStatuses(voteStatuses)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class VoteStatus {
        private Long bestPlaceId;
        private Integer votes;
        private String placeName;
        private Double latitude;
        private Double longitude;
        private Boolean isVoted;

        public static VoteResponse.VoteStatus toStatusDto(BestPlace bestPlace, Boolean isVoted) {
            return VoteStatus.builder()
                    .bestPlaceId(bestPlace.getBestPlaceId())
                    .votes(bestPlace.getSelectPlaces().size())
                    .placeName(bestPlace.getPlaceName())
                    .latitude(bestPlace.getLatitude())
                    .longitude(bestPlace.getLongitude())
                    .isVoted(isVoted)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class SelectPlaceUser {
        private Long participationId;
        private Long userId;
        private String nickName;
        private Boolean isAdmin;

        public static VoteResponse.SelectPlaceUser response(Participation participation, Boolean isAdmin) {
            return SelectPlaceUser.builder()
                    .participationId(participation.getParticipationId())
                    .userId(participation.getUserId())
                    .nickName(participation.getUserName())
                    .isAdmin(isAdmin)
                    .build();
        }
    }
}
