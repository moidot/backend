package com.moim.backend.domain.groupvote.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Optional;

public class VoteServiceRequest {


    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Create {
        @NotNull(message = "익명 여부 값은 필수입니다.")
        private Boolean isAnonymous;
        @NotNull(message = "중복 선택 여부 값은 필수입니다.")
        private Boolean isEnabledMultipleChoice;
        @DateTimeFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
        private Optional<LocalDateTime> endAt;
    }
}
