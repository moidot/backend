package com.moim.backend.domain.spacevote.request.controller;

import com.moim.backend.domain.spacevote.request.service.VoteCreateServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor
@Getter
public class VoteCreateRequest {

    @NotNull(message = "익명 여부 값은 필수입니다.")
    private Boolean isAnonymous;

    @NotNull(message = "중복 선택 여부 값은 필수입니다.")
    private Boolean isEnabledMultipleChoice;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;

    public VoteCreateServiceRequest toServiceRequest() {
        return VoteCreateServiceRequest.builder()
                .isAnonymous(isAnonymous)
                .isEnabledMultipleChoice(isEnabledMultipleChoice)
                .endAt(endAt)
                .build();
    }

    public static VoteCreateRequest toRequest(Boolean isAnonymous, Boolean isEnabledMultipleChoice, LocalDateTime endAt) {
        return new VoteCreateRequest(isAnonymous, isEnabledMultipleChoice, endAt);
    }

}
