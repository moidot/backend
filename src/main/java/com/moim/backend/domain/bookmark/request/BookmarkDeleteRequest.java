package com.moim.backend.domain.bookmark.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookmarkDeleteRequest {
    private List<Long> bookmarkIds;

    public BookmarkDeleteServiceRequest toServiceRequest() {
        return BookmarkDeleteServiceRequest.builder()
                .bookmarkIds(bookmarkIds)
                .build();
    }
}
