package com.example.market.docs.comment;

import com.example.market.api.controller.comment.CommentController;
import com.example.market.docs.RestDocsSupport;
import com.example.market.api.controller.comment.request.CommentCreateRequestDto;
import com.example.market.api.controller.comment.request.CommentReplyRequestDto;
import com.example.market.api.controller.comment.request.CommentUpdateRequestDto;
import com.example.market.api.controller.comment.response.CommentResponse;
import com.example.market.service.comment.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentControllerDocsTest extends RestDocsSupport {

    private final CommentService commentService = mock(CommentService.class);

    @Override
    protected Object initController() {
        return new CommentController(commentService);
    }

    @DisplayName("댓글 등록 API")
    @Test
    void createComment() throws Exception {
        Authentication authentication = getAuthentication();

        CommentCreateRequestDto request = CommentCreateRequestDto.builder()
                .content("댓글내용")
                .build();

        given(commentService.create(anyLong(), any(CommentCreateRequestDto.class), anyLong()))
                .willReturn(createCommentResponse(1L, "작성자", request.getContent(), null));

        mockMvc.perform(
                        post("/items/{itemId}/comments", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comment-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("content").type(STRING)
                                        .description("댓글내용")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.id").type(NUMBER)
                                        .description("댓글 ID"),
                                fieldWithPath("data.content").type(STRING)
                                        .description("댓글내용"),
                                fieldWithPath("data.username").type(STRING)
                                        .description("작성자"),
                                fieldWithPath("data.itemId").type(NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("data.reply").type(NULL)
                                        .optional()
                                        .description("답글")
                        )
                ));
    }

    @DisplayName("댓글 페이징 조회 API")
    @Test
    void readCommentList() throws Exception {
        List<CommentResponse> commentResponses = List.of(
                createCommentResponse(1L, "유저1", "댓글내용1", "답글1"),
                createCommentResponse(2L, "유저2", "댓글내용2", null),
                createCommentResponse(3L, "유저3", "댓글내용3", null)
        );

        Page<CommentResponse> result = new PageImpl<>(commentResponses);
        given(commentService.readCommentList(anyLong(), anyInt(), anyInt()))
                .willReturn(result);

        mockMvc.perform(
                        get("/items/{itemId}/comments", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comment-read-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page")
                                        .description("페이지")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.content[].id").type(NUMBER)
                                        .description("댓글 ID"),
                                fieldWithPath("data.content[].content").type(STRING)
                                        .description("댓글내용"),
                                fieldWithPath("data.content[].reply").type(STRING)
                                        .optional()
                                        .description("답글"),
                                fieldWithPath("data.content[].itemId").type(NUMBER)
                                        .description("아이템 ID"),
                                fieldWithPath("data.content[].username").type(STRING)
                                        .description("작성자"),

                                fieldWithPath("data.last").
                                        description("마지막 페이지인지 여부"),
                                fieldWithPath("data.totalPages").
                                        description("전체 페이지 개수"),
                                fieldWithPath("data.totalElements").
                                        description("테이블 총 데이터 개수"),
                                fieldWithPath("data.first").
                                        description("첫번째 페이지인지 여부"),
                                fieldWithPath("data.numberOfElements").
                                        description("요청 페이지에서 조회 된 데이터 개수"),
                                fieldWithPath("data.number").
                                        description("현재 페이지 번호"),
                                fieldWithPath("data.size").
                                        description("한 페이지당 조회할 데이터 개수"),

                                fieldWithPath("data.sort.sorted").
                                        description("정렬 됐는지 여부"),
                                fieldWithPath("data.sort.unsorted").
                                        description("정렬 안 됐는지 여부"),
                                fieldWithPath("data.sort.empty").
                                        description("데이터가 비었는지 여부"),

                                fieldWithPath("data.empty").
                                        description("데이터가 비었는지 여부"),

                                fieldWithPath("data.pageable").
                                        description("페이징 정보")
                        )
                ));
    }

    @DisplayName("댓글 수정 API")
    @Test
    void updateComment() throws Exception {
        Authentication authentication = getAuthentication();

        CommentUpdateRequestDto request = CommentUpdateRequestDto.builder()
                .content("댓글수정")
                .build();

        given(commentService.updateComment(anyLong(), anyLong(), any(CommentUpdateRequestDto.class), anyLong()))
                .willReturn(updateCommentResponse(1L, "작성자", request.getContent(), "답글"));

        mockMvc.perform(
                        put("/items/{itemId}/comments/{commentId}", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comment-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("content").type(STRING)
                                        .description("댓글내용")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.id").type(NUMBER)
                                        .description("댓글 ID"),
                                fieldWithPath("data.content").type(STRING)
                                        .description("댓글내용"),
                                fieldWithPath("data.username").type(STRING)
                                        .description("작성자"),
                                fieldWithPath("data.itemId").type(NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("data.reply").type(STRING)
                                        .optional()
                                        .description("답글")
                        )
                ));
    }

    @DisplayName("댓글 삭제 API")
    @Test
    void deleteComment() throws Exception {
        Authentication authentication = getAuthentication();

        given(commentService.deleteComment(anyLong(), anyLong(), anyLong()))
                .willReturn(createCommentResponse(1L, "작성자", "댓글내용", null));

        mockMvc.perform(
                        delete("/items/{itemId}/comments/{commentId}", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comment-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.id").type(NUMBER)
                                        .description("댓글 ID"),
                                fieldWithPath("data.content").type(STRING)
                                        .description("댓글내용"),
                                fieldWithPath("data.username").type(STRING)
                                        .description("작성자"),
                                fieldWithPath("data.itemId").type(NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("data.reply").type(STRING)
                                        .optional()
                                        .description("답글")
                        )
                ));
    }

    @DisplayName("답글 작성 API")
    @Test
    void createReply() throws Exception {
        Authentication authentication = getAuthentication();

        CommentReplyRequestDto request = CommentReplyRequestDto.builder()
                .reply("답글")
                .build();

        given(commentService.updateCommentReply(anyLong(), anyLong(), any(CommentReplyRequestDto.class), anyLong()))
                .willReturn(createCommentResponse(1L, "작성자", "댓글내용", request.getReply()));

        mockMvc.perform(
                        put("/items/{itemId}/comments/{commentId}/reply", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comment-reply",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("reply").type(STRING)
                                        .description("답글")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.id").type(NUMBER)
                                        .description("댓글 ID"),
                                fieldWithPath("data.content").type(STRING)
                                        .description("댓글내용"),
                                fieldWithPath("data.username").type(STRING)
                                        .description("작성자"),
                                fieldWithPath("data.itemId").type(NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("data.reply").type(STRING)
                                        .description("답글")
                        )
                ));
    }

    private Authentication getAuthentication() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("1");
        return authentication;
    }

    private CommentResponse createCommentResponse(final Long id, final String username, final String content, final String reply) {
        return CommentResponse.builder()
                .id(id)
                .content(content)
                .username(username)
                .itemId(1L)
                .reply(reply)
                .build();
    }
    private CommentResponse updateCommentResponse(final Long id, final String username, final String content, final String reply) {
        return CommentResponse.builder()
                .id(id)
                .content(content)
                .username(username)
                .itemId(1L)
                .reply(reply)
                .build();
    }
}
