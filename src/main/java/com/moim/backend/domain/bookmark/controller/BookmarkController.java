package com.moim.backend.domain.bookmark.controller;

import com.moim.backend.domain.bookmark.request.BookmarkSaveRequest;
import com.moim.backend.domain.bookmark.response.BookmarkDetailResponse;
import com.moim.backend.domain.bookmark.response.BookmarkSaveResponse;
import com.moim.backend.domain.bookmark.service.BookmarkService;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.global.auth.Login;
import com.moim.backend.global.common.CustomResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/bookmark")
    public CustomResponseEntity<BookmarkSaveResponse> saveBookmark(
            @RequestBody BookmarkSaveRequest request, @Login Users user
    ) {
        return CustomResponseEntity.success(bookmarkService.saveBookmark(request.toServiceRequest(), user));
    }

    @GetMapping("/bookmark")
    public CustomResponseEntity<List<BookmarkDetailResponse>> readBookmark(
            @Login Users user
    ) {
        return CustomResponseEntity.success(bookmarkService.readBookmark(user));
    }
}
