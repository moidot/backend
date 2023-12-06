package com.moim.backend.domain.bookmark.controller;

import com.moim.backend.domain.bookmark.request.BookmarkDeleteRequest;
import com.moim.backend.domain.bookmark.request.BookmarkSaveRequest;
import com.moim.backend.domain.bookmark.response.BookmarkDetailResponse;
import com.moim.backend.domain.bookmark.response.BookmarkSaveResponse;
import com.moim.backend.domain.bookmark.service.BookmarkService;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.auth.Login;
import com.moim.backend.global.common.CustomResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/bookmark")
    public CustomResponseEntity<BookmarkSaveResponse> saveBookmark(
            @RequestBody @Valid BookmarkSaveRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(bookmarkService.saveBookmark(request.toServiceRequest(), user));
    }

    @GetMapping("/bookmark")
    public CustomResponseEntity<List<BookmarkDetailResponse>> readBookmark(
            @Login Users user
    ) {
        return CustomResponseEntity.success(bookmarkService.readBookmark(user));
    }

    @DeleteMapping("/bookmark")
    public CustomResponseEntity<Void> deleteBookmarks(
            @RequestBody @Valid BookmarkDeleteRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(bookmarkService.deleteBookmarks(request.toServiceRequest(), user));
    }
}
