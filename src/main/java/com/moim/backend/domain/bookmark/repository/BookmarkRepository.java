package com.moim.backend.domain.bookmark.repository;

import com.moim.backend.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);

    Optional<Bookmark> findByBookmarkIdAndUserId(Long bookmarkId, Long userId);
}
