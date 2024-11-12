package com.moim.backend.domain.space.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathDto {

    @JsonProperty("x")
    private Double longitude;
    @JsonProperty("y")
    private Double latitude;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        PathDto comparePath = (PathDto) obj;
        return Double.compare(latitude, comparePath.getLatitude()) == 0
                && Double.compare(longitude, comparePath.getLongitude()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
