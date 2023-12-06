package com.moim.backend.domain.bookmark.service;

import com.moim.backend.domain.bookmark.entity.Bookmark;
import com.moim.backend.domain.bookmark.repository.BookmarkRepository;
import com.moim.backend.domain.bookmark.request.BookmarkDeleteServiceRequest;
import com.moim.backend.domain.bookmark.request.BookmarkSaveServiceRequest;
import com.moim.backend.domain.bookmark.response.BookmarkDetailResponse;
import com.moim.backend.domain.bookmark.response.BookmarkSaveResponse;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.common.Result;
import com.moim.backend.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.moim.backend.global.common.Result.*;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;

    public BookmarkSaveResponse saveBookmark(BookmarkSaveServiceRequest request, Users user) {
        Bookmark bookmark = bookmarkRepository.save(
                Bookmark.builder()
                        .userId(user.getUserId())
                        .locationName(request.getLocationName())
                        .address(request.getAddress())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .build()
        );
        return BookmarkSaveResponse.response(bookmark);
    }

    public List<BookmarkDetailResponse> readBookmark(Users user) {
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(user.getUserId());
        return bookmarks.stream()
                .map(BookmarkDetailResponse::response)
                .toList();
    }

    public Void deleteBookmarks(BookmarkDeleteServiceRequest request, Users user) {
        for (Long bookmarkId : request.getBookmarkIds()) {
            Bookmark bookmark = getMyBookmark(user, bookmarkId);
            bookmarkRepository.delete(bookmark);
        }
        return null;
    }

    private Bookmark getMyBookmark(Users user, Long bookmarkId) {
        return bookmarkRepository.findByBookmarkIdAndUserId(bookmarkId, user.getUserId()).orElseThrow(
                () -> new CustomException(FAIL)
        );
    }
}
