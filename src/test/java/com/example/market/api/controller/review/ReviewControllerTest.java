package com.example.market.api.controller.review;

import com.example.market.ControllerTestSupport;
import com.example.market.api.controller.review.request.ReviewCreateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewControllerTest extends ControllerTestSupport {

    @DisplayName("판매 완료 상품에 리뷰를 등록한다.")
    @Test
    void createReview() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .content("리뷰내용")
                .score(1.0)
                .build();

        // when // then
        mockMvc.perform(
                        post("/item/{itemId}/{revieweeId}/review", 1L, 1L).with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("판매 완료 상품에 리뷰를 등록할 때, 내용은 필수값이다.")
    @Test
    void createReviewWithoutContent() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
//                .content("리뷰내용")
                .score(1.0)
                .build();

        // when // then
        mockMvc.perform(
                        post("/item/{itemId}/{revieweeId}/review", 1L, 1L).with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("리뷰 내용은 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("판매 완료 상품에 리뷰를 등록할 때, 평가 점수는 필수값이다.")
    @Test
    void createReviewWithoutScore() throws Exception {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .content("리뷰내용")
//                .score(1.0)
                .build();

        // when // then
        mockMvc.perform(
                        post("/item/{itemId}/{revieweeId}/review", 1L, 1L).with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("평가 점수는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}