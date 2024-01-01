package com.moim.backend.domain.bookmark.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor()
@NoArgsConstructor
@Getter
public class BookmarkSaveRequest {
    @NotNull(message = "장소 이름은 필수입니다.")
    private String locationName;
    @NotNull(message = "주소는 필수입니다.")
    private String address;
    @NotNull(message = "위도는 필수입니다.")
    private Double latitude;
    @NotNull(message = "경도는 필수입니다.")
    private Double longitude;

    public BookmarkSaveServiceRequest toServiceRequest() {
        return BookmarkSaveServiceRequest.builder()
                .locationName(locationName)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
