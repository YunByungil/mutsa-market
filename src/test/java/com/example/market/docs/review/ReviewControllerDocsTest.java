package com.example.market.docs.review;

import com.example.market.api.controller.item.request.ItemCreateRequestDto;
import com.example.market.api.controller.item.response.ItemResponse;
import com.example.market.api.controller.review.ReviewController;
import com.example.market.api.controller.review.request.ReviewCreateRequest;
import com.example.market.api.controller.review.response.ReviewResponse;
import com.example.market.docs.RestDocsSupport;
import com.example.market.domain.review.ReviewerType;
import com.example.market.service.review.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

import static com.example.market.domain.item.ItemStatus.SALE;
import static com.example.market.domain.review.ReviewerType.SELLER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReviewControllerDocsTest extends RestDocsSupport {

    private final ReviewService reviewService = mock(ReviewService.class);

    @Override
    protected Object initController() {
        return new ReviewController(reviewService);
    }

    @DisplayName("판매 상품 등록 API")
    @Test
    void createItem() throws Exception {
        Authentication authentication = getAuthentication();

        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .content("리뷰내용")
                .score(1.0)
                .build();

        given(reviewService.createReview(anyLong(), anyLong(), any(ReviewCreateRequest.class), anyLong()))
                .willReturn(ReviewResponse.builder()
                        .id(1L)
                        .reviewerId(1L)
                        .revieweeId(2L)
                        .itemId(1L)
                        .reviewerType(SELLER)
                        .content("리뷰내용")
                        .build());

        mockMvc.perform(
                        post("/item/{itemId}/{revieweeId}/review", 1L, 2L)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("review-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("content").type(STRING)
                                        .description("리뷰내용"),
                                fieldWithPath("score").type(NUMBER)
                                        .description("평가점수")
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
                                        .description("리뷰 ID"),
                                fieldWithPath("data.reviewerId").type(NUMBER)
                                        .description("리뷰어 ID"),
                                fieldWithPath("data.revieweeId").type(NUMBER)
                                        .description("리뷰이 ID"),
                                fieldWithPath("data.itemId").type(NUMBER)
                                        .description("아이템 ID"),
                                fieldWithPath("data.reviewerType").type(STRING)
                                        .description("리뷰어 타입(판매자/구매자)"),
                                fieldWithPath("data.content").type(STRING)
                                        .description("리뷰 내용")
                        )
                ));
    }

    private Authentication getAuthentication() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("1");
        return authentication;
    }
}
