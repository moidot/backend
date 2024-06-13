package com.moim.backend.domain.bookmark.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BookmarkDeleteServiceRequest {
    private List<Long> bookmarkIds;
}
