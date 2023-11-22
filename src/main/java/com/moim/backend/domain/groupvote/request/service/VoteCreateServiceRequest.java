package com.moim.backend.domain.groupvote.request.service;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Builder
public class VoteCreateServiceRequest {
    @NotNull(message = "익명 여부 값은 필수입니다.")
    private Boolean isAnonymous;
    @NotNull(message = "중복 선택 여부 값은 필수입니다.")
    private Boolean isEnabledMultipleChoice;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;

    public Optional<LocalDateTime> getEndAt() {
        return Optional.ofNullable(endAt);
    }
}
