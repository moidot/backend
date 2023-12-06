package com.moim.backend.docs.bookmark;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.moim.backend.RestDocsSupport;
import com.moim.backend.domain.bookmark.controller.BookmarkController;
import com.moim.backend.domain.bookmark.entity.Bookmark;
import com.moim.backend.domain.bookmark.request.BookmarkDeleteRequest;
import com.moim.backend.domain.bookmark.request.BookmarkSaveRequest;
import com.moim.backend.domain.bookmark.response.BookmarkDetailResponse;
import com.moim.backend.domain.bookmark.response.BookmarkSaveResponse;
import com.moim.backend.domain.bookmark.service.BookmarkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookmarkControllerDocsTest extends RestDocsSupport {

    private final BookmarkService bookmarkService = mock(BookmarkService.class);

    @Override
    protected Object initController() {
        return new BookmarkController(bookmarkService);
    }

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

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("북마크 API")
                .summary("북마크 저장 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .requestFields(
                        fieldWithPath("locationName").type(STRING).description("장소 이름"),
                        fieldWithPath("address").type(STRING).description("주소"),
                        fieldWithPath("latitude").type(NUMBER).description("위도"),
                        fieldWithPath("longitude").type(NUMBER).description("경도"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메시지"),
                        fieldWithPath("data.bookmarkId").type(NUMBER).description("북마크 ID"),
                        fieldWithPath("data.locationName").type(STRING).description("장소 이름"),
                        fieldWithPath("data.address").type(STRING).description("주소"),
                        fieldWithPath("data.latitude").type(NUMBER).description("위도"),
                        fieldWithPath("data.longitude").type(NUMBER).description("경도"))
                .build();

        RestDocumentationResultHandler document = documentHandler("saveBookmark", prettyPrint(), prettyPrint(), parameters);

        given(bookmarkService.saveBookmark(any(), any()))
                .willReturn(
                        BookmarkSaveResponse.builder()
                                .bookmarkId(1L)
                                .locationName("삼성 서비스센터")
                                .address("경기도 양주시 부흥로 20")
                                .latitude(123.123456)
                                .longitude(123.123456)
                                .build()
                );

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/bookmark")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("내 북마크 확인 API")
    @Test
    void readBookmark() throws Exception {
        // given
        BookmarkDetailResponse bookmark1 = BookmarkDetailResponse.builder()
                .bookmarkId(1L)
                .locationName("삼성 서비스 센터")
                .address("경기도 양주시 부흥로")
                .latitude(123.123456)
                .longitude(123.123456)
                .build();

        BookmarkDetailResponse bookmark2 = BookmarkDetailResponse.builder()
                .bookmarkId(2L)
                .locationName("LG 서비스 센터")
                .address("경기도 양주시 부흥로")
                .latitude(123.123456)
                .longitude(123.123456)
                .build();

        BookmarkDetailResponse bookmark3 = BookmarkDetailResponse.builder()
                .bookmarkId(3L)
                .locationName("SONY 서비스 센터")
                .address("경기도 양주시 부흥로")
                .latitude(123.123456)
                .longitude(123.123456)
                .build();

        given(bookmarkService.readBookmark(any()))
                .willReturn(
                        List.of(bookmark1, bookmark2, bookmark3)
                );

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("북마크 API")
                .summary("내 북마크 확인 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메시지"),
                        fieldWithPath("data[].bookmarkId").type(NUMBER).description("북마크 ID"),
                        fieldWithPath("data[].locationName").type(STRING).description("장소 이름"),
                        fieldWithPath("data[].address").type(STRING).description("주소"),
                        fieldWithPath("data[].latitude").type(NUMBER).description("위도"),
                        fieldWithPath("data[].longitude").type(NUMBER).description("경도")
                )
                .build();

        RestDocumentationResultHandler document = documentHandler("readBookmark", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/bookmark")
                                .header("Authorization", "JWT AccessToken")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }

    @DisplayName("북마크 삭제하기 API")
    @Test
    void deleteBookmarks() throws Exception {
        // given
        BookmarkDeleteRequest request = new BookmarkDeleteRequest(List.of(1L, 2L, 3L));

        ResourceSnippetParameters parameters = ResourceSnippetParameters.builder()
                .tag("북마크 API")
                .summary("북마크 삭제하기 API")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Swagger 요청시 해당 입력칸이 아닌 우측 상단 자물쇠 " +
                                        "또는 Authorize 버튼을 이용해 토큰을 넣어주세요"))
                .requestFields(
                        fieldWithPath("bookmarkIds").type(ARRAY).description("북마크 ID 리스트"))
                .responseFields(
                        fieldWithPath("code").type(NUMBER).description("상태 코드"),
                        fieldWithPath("message").type(STRING).description("상태 메시지"))
                .build();

        RestDocumentationResultHandler document = documentHandler("deleteBookmarks", prettyPrint(), parameters);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/bookmark")
                                .header("Authorization", "JWT AccessToken")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document);
    }
}
