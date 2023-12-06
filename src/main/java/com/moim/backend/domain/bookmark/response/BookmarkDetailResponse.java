package com.moim.backend.domain.bookmark.response;

import com.moim.backend.domain.bookmark.entity.Bookmark;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BookmarkDetailResponse {
    private Long bookmarkId;
    private String locationName;
    private String address;
    private Double latitude;
    private Double longitude;

    public static BookmarkDetailResponse response(Bookmark bookmark) {
        return BookmarkDetailResponse.builder()
                .bookmarkId(bookmark.getBookmarkId())
                .locationName(bookmark.getLocationName())
                .address(bookmark.getAddress())
                .latitude(bookmark.getLatitude())
                .longitude(bookmark.getLongitude())
                .build();
    }
}
