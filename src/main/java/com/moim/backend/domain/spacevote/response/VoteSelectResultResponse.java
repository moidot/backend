package com.moim.backend.domain.spacevote.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moim.backend.domain.spacevote.entity.Vote;
import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Space;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class VoteSelectResultResponse {
    private Long groupId;
    private String groupName;
    private String groupDate;
    @JsonInclude(value = NON_NULL)
    private String confirmPlace;
    private Long voteId;
    private Boolean isClosed;
    private Boolean isAnonymous;
    private Boolean isEnabledMultipleChoice;
    private String endAt;
    private Boolean isVotingParticipant;
    private Integer totalVoteNum;
    private List<VoteStatus> voteStatuses;

    public static VoteSelectResultResponse response(
            Space group, Vote vote, List<VoteStatus> voteStatuses, int totalVoteNum, Boolean isVotingParticipant
    ) {

        if (Optional.ofNullable(vote).isEmpty()) {
            vote = Vote.builder()
                    .voteId(-1L)
                    .isClosed(false)
                    .isAnonymous(false)
                    .isEnabledMultipleChoice(false)
                    .build();
        }

        String groupDate = group.getDate()
                .map(date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .orElse("none");

        String endAt = Optional.ofNullable(vote.getEndAt())
                .map(time -> time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                .orElse("none");

        return VoteSelectResultResponse.builder()
                .groupId(group.getSpaceId())
                .groupName(group.getName())
                .groupDate(groupDate)
                .confirmPlace(group.getPlace())
                .voteId(vote.getVoteId())
                .isClosed(vote.getIsClosed())
                .isAnonymous(vote.getIsAnonymous())
                .isEnabledMultipleChoice(vote.getIsEnabledMultipleChoice())
                .endAt(endAt)
                .isVotingParticipant(isVotingParticipant)
                .totalVoteNum(totalVoteNum)
                .voteStatuses(voteStatuses)
                .build();
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

        public static VoteStatus toStatusDto(BestPlace bestPlace, Boolean isVoted) {
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
}
