package com.example.market.service.review;

import com.example.market.api.controller.review.request.ReviewCreateRequest;
import com.example.market.api.controller.review.response.ReviewResponse;
import com.example.market.domain.item.Item;
import com.example.market.domain.item.ItemRepository;
import com.example.market.domain.item.ItemStatus;
import com.example.market.domain.review.Review;
import com.example.market.domain.review.ReviewRepository;
import com.example.market.domain.review.ReviewerType;
import com.example.market.domain.user.User;
import com.example.market.domain.user.UserRepository;
import com.example.market.exception.ErrorCode;
import com.example.market.exception.MarketAppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.market.domain.item.ItemStatus.SOLD;
import static com.example.market.exception.ErrorCode.*;
import static com.example.market.exception.ErrorCode.NOT_MATCH_ITEM_STATUS_SOLD;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ReviewResponse createReview(final Long reviewerId,
                                       final Long revieweeId,
                                       final ReviewCreateRequest request,
                                       final Long itemId) {
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

        User reviewee = userRepository.findById(revieweeId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));

        // 리뷰를 작성할 때, 이미 내가 작성한 리뷰가 있으면 예외가 발생한다.
        validateDuplicateReview(reviewerId, revieweeId, itemId);

        ReviewerType reviewerType = getReviewerType(reviewerId, item);

        // 구매자가 먼저 리뷰를 작성할 수 없다. 이러한 로직은 어떻게 짤까?
        /*
        1. 리뷰를 생성할 때, 이미 리뷰가 존재하는지 체크한다.
        2. 리뷰어 타입이 판매자인지 구매자인지 구분한다.
        3. '구매자'일 경우, 판매자가 리뷰를 작성했는지 체크한다. 존재하지 않으면 예외가 발생한다.
         */
        if (reviewerType.equals(ReviewerType.BUYER)) {
            if (!reviewRepository.existsByItemIdAndReviewerIdAndRevieweeId(itemId, revieweeId, reviewerId)) {
                throw new MarketAppException(NOT_FOUND_BUY, NOT_FOUND_BUY.getMessage());
            }
        }

        // 아이템의 상태가 SOLD인지 확인하는 메서드도 필요하다.
        validateItemStatusIsSold(item);

        Review review = Review.createReview(reviewer, reviewee, item, request, reviewerType);
        Review savedReview = reviewRepository.save(review);

        return ReviewResponse.of(savedReview);
    }

    private void validateDuplicateReview(final Long reviewerId, final Long revieweeId, final Long itemId) {
        if (reviewRepository.existsByItemIdAndReviewerIdAndRevieweeId(itemId, reviewerId, revieweeId)) {
            throw new MarketAppException(ALREADY_REVIEW, ALREADY_REVIEW.getMessage());
        }
    }

    private ReviewerType getReviewerType(final Long reviewerId, final Item item) {
        if (item.getUser().getId().equals(reviewerId)) {
            return ReviewerType.SELLER;
        }

        return ReviewerType.BUYER;
    }

    private void validateItemStatusIsSold(final Item item) {
        if (!item.getStatus().equals(SOLD)) {
            throw new MarketAppException(NOT_MATCH_ITEM_STATUS_SOLD, NOT_MATCH_ITEM_STATUS_SOLD.getMessage());
        }
    }
}
