package com.moim.backend.domain.groupvote.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Optional;

public class VoteRequest {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Create {

        @NotNull(message = "익명 여부 값은 필수입니다.")
        private Boolean isAnonymous;

        @NotNull(message = "중복 선택 여부 값은 필수입니다.")
        private Boolean isEnabledMultipleChoice;

        @DateTimeFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
        private LocalDateTime endAt;

        public VoteServiceRequest.Create toServiceRequest() {
            return VoteServiceRequest.Create.builder()
                    .isAnonymous(isAnonymous)
                    .isEnabledMultipleChoice(isEnabledMultipleChoice)
                    .endAt(Optional.ofNullable(endAt))
                    .build();
        }
    }
}
