package com.moim.backend.domain.bookmark.controller;

import com.moim.backend.domain.ControllerTestSupport;
import com.moim.backend.domain.bookmark.request.BookmarkDeleteRequest;
import com.moim.backend.domain.bookmark.request.BookmarkSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookmarkControllerTest extends ControllerTestSupport {

    @DisplayName("북마크 저장 API")
    @Test
    void saveBookmark() throws Exception {
        // given
        BookmarkSaveRequest request = new BookmarkSaveRequest(
                "삼성 서비스센터",
                "경기도 양주시 부흥로 20",
                123.123456,
                123.123456
        );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/bookmark")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }


    @DisplayName("내 북마크 확인 API")
    @Test
    void readBookmark() throws Exception {
        // given
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/bookmark")
                                .header("Authorization", "JWT AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("북마크 삭제하기 API")
    @Test
    void deleteBookmarks() throws Exception {
        // given
        BookmarkDeleteRequest request = new BookmarkDeleteRequest(List.of(1L, 2L, 3L));

        // when // then
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/bookmark")
                        .header("Authorization", "JWT AccessToken")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk());
    }
}