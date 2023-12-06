package com.moim.backend.domain.bookmark.response;

import com.moim.backend.domain.bookmark.entity.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BookmarkSaveResponse {
    private Long bookmarkId;
    private String locationName;
    private String address;
    private Double latitude;
    private Double longitude;

    public static BookmarkSaveResponse response(Bookmark bookmark) {
        return BookmarkSaveResponse.builder()
                .bookmarkId(bookmark.getBookmarkId())
                .locationName(bookmark.getLocationName())
                .address(bookmark.getAddress())
                .latitude(bookmark.getLatitude())
                .longitude(bookmark.getLongitude())
                .build();
    }
}
