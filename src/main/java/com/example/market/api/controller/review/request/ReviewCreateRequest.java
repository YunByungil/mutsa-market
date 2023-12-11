package com.example.market.api.controller.review.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReviewCreateRequest {

    @NotNull(message = "평가 점수는 필수입니다.")
    private Double score;

    @NotBlank(message = "리뷰 내용은 필수입니다.")
    private String content;

    @Builder
    public ReviewCreateRequest(final Double score, final String content) {
        this.score = score;
        this.content = content;
    }
}
