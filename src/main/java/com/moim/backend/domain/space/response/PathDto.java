package com.moim.backend.domain.space.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathDto {

    @JsonProperty("x")
    private Double longitude;
    @JsonProperty("y")
    private Double latitude;

}
