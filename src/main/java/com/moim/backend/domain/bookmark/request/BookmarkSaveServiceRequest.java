package com.moim.backend.domain.bookmark.request;

import lombok.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Builder
public class BookmarkSaveServiceRequest {
    private String locationName;
    private String address;
    private Double latitude;
    private Double longitude;
}
