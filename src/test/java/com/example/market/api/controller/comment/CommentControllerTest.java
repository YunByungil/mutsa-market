package com.example.market.api.controller.comment;

import com.example.market.ControllerTestSupport;
import com.example.market.api.controller.comment.request.CommentCreateRequestDto;
import com.example.market.api.controller.comment.request.CommentReplyRequestDto;
import com.example.market.api.controller.comment.request.CommentUpdateRequestDto;
import com.example.market.api.controller.comment.response.CommentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends ControllerTestSupport {

    @DisplayName("댓글을 작성한다.")
    @Test
    void createComment() throws Exception {
        // given
        CommentCreateRequestDto createDto = CommentCreateRequestDto.builder()
                .content("내용")
                .build();

        // when // then
        mockMvc.perform(
                        post("/items/{itemId}/comments", 1L).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("댓글을 작성할 때, 댓글 내용은 꼭 입력해야한다.")
    @Test
    void createCommentWithEmptyContent() throws Exception {
        // given
        CommentCreateRequestDto createDto = CommentCreateRequestDto.builder()
//                .content("내용")
                .build();

        // when // then
        mockMvc.perform(
                        post("/items/{itemId}/comments", 1L).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("댓글 내용은 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("댓글을 수정한다.")
    @Test
    void updateComment() throws Exception {
        // given
        CommentUpdateRequestDto request = CommentUpdateRequestDto.builder()
                .content("수정내용")
                .build();

        // when // then
        mockMvc.perform(
                        put("/items/{itemId}/comments/{commentId}", 1, 1).with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("댓글을 수정할 때, 댓글 내용은 꼭 입력해야 한다.")
    @Test
    void updateCommentWithEmptyContent() throws Exception {
        // given
        CommentUpdateRequestDto request = CommentUpdateRequestDto.builder()
//                .content("수정내용")
                .build();

        // when // then
        mockMvc.perform(
                        put("/items/{itemId}/comments/{commentId}", 1, 1).with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("댓글 내용은 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("댓글을 삭제한다.")
    @Test
    void deleteComment() throws Exception {
        // when // then
        mockMvc.perform(
                        delete("/items/{itemId}/comments/{commentId}", 1, 1).with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("답글(reply)을 작성한다.")
    @Test
    void createReply() throws Exception {
        // given
        CommentReplyRequestDto request = CommentReplyRequestDto.builder()
                .reply("답글내용")
                .build();

        // when // then
        mockMvc.perform(
                        put("/items/{itemId}/comments/{commentId}/reply", 1, 1).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("답글(reply)을 작성할 때, 답글 내용은 꼭 입력해야 한다.")
    @Test
    void createReplyWithEmptyContent() throws Exception {
        // given
        CommentReplyRequestDto request = CommentReplyRequestDto.builder()
//                .reply("답글내용")
                .build();

        // when // then
        mockMvc.perform(
                        put("/items/{itemId}/comments/{commentId}/reply", 1, 1).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("답글 내용은 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("등록된 댓글을 조회한다.")
    @Test
    void readAllComments() throws Exception {
        // given
        Page<CommentResponse> result = new PageImpl<>(emptyList());

        BDDMockito.when(commentService.readCommentList(anyLong(), anyInt(), anyInt())).thenReturn(result);

        // when // then
        mockMvc.perform(
                        get("/items/{itemId}/comments", 1L).with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }
}