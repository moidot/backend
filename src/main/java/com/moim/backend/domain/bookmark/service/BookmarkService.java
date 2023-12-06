package com.moim.backend.domain.bookmark.service;

import com.moim.backend.domain.bookmark.entity.Bookmark;
import com.moim.backend.domain.bookmark.repository.BookmarkRepository;
import com.moim.backend.domain.bookmark.request.BookmarkSaveServiceRequest;
import com.moim.backend.domain.bookmark.response.BookmarkDetailResponse;
import com.moim.backend.domain.bookmark.response.BookmarkSaveResponse;
import com.moim.backend.domain.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
