package com.example.market.domain.review;

import com.example.market.api.controller.review.request.ReviewCreateRequest;
import com.example.market.domain.item.Item;
import com.example.market.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id")
    private User reviewee;

    private Double score;

    private String content;

    @Enumerated(EnumType.STRING)
    private ReviewerType reviewerType;

    @Builder
    public Review(final Item item, final User reviewer, final User reviewee, final Double score, final String content, final ReviewerType reviewerType) {
        this.item = item;
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.score = score;
        this.content = content;
        this.reviewerType = reviewerType;
    }

    public static Review createReview(final User reviewer, final User reviewee,
                                      final Item item,
                                      final ReviewCreateRequest request,
                                      final ReviewerType reviewerType) {
        return Review.builder()
                .reviewerType(reviewerType)
                .item(item)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .content(request.getContent())
                .score(request.getScore())
                .build();
    }
}
