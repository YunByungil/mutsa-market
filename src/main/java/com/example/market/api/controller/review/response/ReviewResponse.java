package com.example.market.api.controller.review.response;

import com.example.market.domain.review.Review;
import com.example.market.domain.review.ReviewerType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReviewResponse {

    private Long id;
    private Long itemId;
    private Long reviewerId;
    private Long revieweeId;
    private String content;
    private ReviewerType reviewerType;

    @Builder
    public ReviewResponse(final Long id, final Long itemId, final Long reviewerId, final Long revieweeId, final String content, final ReviewerType reviewerType) {
        this.id = id;
        this.itemId = itemId;
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.content = content;
        this.reviewerType = reviewerType;
    }

    public static ReviewResponse of(final Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reviewerId(review.getReviewer().getId())
                .itemId(review.getItem().getId())
                .revieweeId(review.getReviewee().getId())
                .content(review.getContent())
                .reviewerType(review.getReviewerType())
                .build();
    }
}
