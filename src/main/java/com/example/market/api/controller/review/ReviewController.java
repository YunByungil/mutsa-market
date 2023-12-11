package com.example.market.api.controller.review;

import com.example.market.api.ApiResponse;
import com.example.market.api.controller.review.request.ReviewCreateRequest;
import com.example.market.api.controller.review.response.ReviewResponse;
import com.example.market.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/item/{itemId}/{revieweeId}/review")
    public ApiResponse<ReviewResponse> createReview(@Valid @RequestBody final ReviewCreateRequest request,
                                                    @PathVariable final Long itemId,
                                                    final Authentication authentication,
                                                    @PathVariable final Long revieweeId) {
        Long reviewerId = Long.parseLong(authentication.getName());

        return ApiResponse.ok(reviewService.createReview(reviewerId, revieweeId, request, itemId));
    }
}
