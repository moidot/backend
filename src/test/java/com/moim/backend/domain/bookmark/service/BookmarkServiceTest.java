package com.moim.backend.domain.bookmark.service;

import com.moim.backend.domain.bookmark.entity.Bookmark;
import com.moim.backend.domain.bookmark.repository.BookmarkRepository;
import com.moim.backend.domain.bookmark.request.BookmarkDeleteRequest;
import com.moim.backend.domain.bookmark.request.BookmarkSaveRequest;
import com.moim.backend.domain.bookmark.response.BookmarkDetailResponse;
import com.moim.backend.domain.bookmark.response.BookmarkSaveResponse;
import com.moim.backend.domain.user.entity.Users;
import com.moim.backend.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookmarkServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private EntityManager em;

    @DisplayName("유저가 북마크를 등록한다.")
    @Test
    void saveBookmark() {
        // given
        Users user = savedUser("test@test.com", "테스트계정");
        BookmarkSaveRequest request = new BookmarkSaveRequest(
                "삼성 서비스센터",
                "경기도 양주시 부흥로 20",
                123.123456,
                123.123456
        );

        // when
        BookmarkSaveResponse response = bookmarkService.saveBookmark(request.toServiceRequest(), user);

        // then
        assertThat(response)
                .extracting("locationName", "address", "latitude", "longitude")
                .contains("삼성 서비스센터", "경기도 양주시 부흥로 20", 123.123456, 123.123456);
    }

    @DisplayName("자신의 북마크를 확인한다.")
    @Test
    void readBookmark() {
        // given
        Users user = savedUser("test@test.com", "테스트계정");
        saveBookmark(user, "1");
        saveBookmark(user, "2");
        saveBookmark(user, "3");

        // when
        List<BookmarkDetailResponse> response = bookmarkService.readBookmark(user);

        // then
        assertThat(response)
                .hasSize(3)
                .extracting("locationName", "address", "latitude", "longitude")
                .containsExactly(
                        tuple("1", "테스트", 123.123456, 123.123456),
                        tuple("2", "테스트", 123.123456, 123.123456),
                        tuple("3", "테스트", 123.123456, 123.123456)
                );
    }

    @DisplayName("유저가 자신이 등록한 북마크를 여러개 제거한다.")
    @Test
    void deleteBookmarks() {
        // given
        Users user = savedUser("test@test.com", "테스트계정");
        Bookmark bookmark1 = saveBookmark(user, "1");
        Bookmark bookmark2 = saveBookmark(user, "2");
        Bookmark bookmark3 = saveBookmark(user, "3");
        List<Long> bookmarkIds = List.of(bookmark1.getBookmarkId(), bookmark2.getBookmarkId(), bookmark3.getBookmarkId());
        BookmarkDeleteRequest request = new BookmarkDeleteRequest(bookmarkIds);

        // when
        bookmarkService.deleteBookmarks(request.toServiceRequest(), user);

        // then
        em.flush();
        em.clear();
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(user.getUserId());

        assertThat(bookmarks.isEmpty()).isTrue();
    }

    private Bookmark saveBookmark(Users user, String locationName) {
        return bookmarkRepository.save(Bookmark.builder()
                .userId(user.getUserId())
                .locationName(locationName)
                .address("테스트")
                .latitude(123.123456)
                .longitude(123.123456)
                .build());
    }

    private Users savedUser(String email, String name) {
        return userRepository.save(
                Users.builder()
                        .email(email)
                        .name(name)
                        .build()
        );
    }
}