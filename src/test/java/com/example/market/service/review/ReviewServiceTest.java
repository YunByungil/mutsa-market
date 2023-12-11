package com.example.market.service.review;

import com.example.market.IntegrationTestSupport;
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
import com.example.market.exception.MarketAppException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.example.market.domain.item.ItemStatus.SALE;
import static com.example.market.domain.item.ItemStatus.SOLD;
import static com.example.market.domain.review.ReviewerType.BUYER;
import static com.example.market.domain.review.ReviewerType.SELLER;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAllInBatch();
        itemRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("상품 판매자가 판매 후, 리뷰를 생성한다.")
    @Test
    void createReviewBySeller() {
        // given
        User seller = createUser();
        User buyer = createUser();
        userRepository.saveAll(List.of(seller, buyer));

        Item item = createItem(seller, 10_000, "제목", "내용", SOLD);
        itemRepository.save(item);

        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .content("리뷰내용")
                .score(1.0)
                .build();

        // when
        ReviewResponse reviewResponse = reviewService.createReview(seller.getId(), buyer.getId(), request, item.getId());

        // then
        assertThat(reviewResponse.getId()).isNotNull();
        assertThat(reviewResponse)
                .extracting("reviewerId", "revieweeId", "content", "reviewerType")
                .containsExactly(seller.getId(), buyer.getId(), request.getContent(), SELLER);
    }

    @DisplayName("상품 판매자가 판매 후, 리뷰를 생성할 때, 아이템 판매 상태가 SOLD가 아니면 예외가 발생한다.")
    @Test
    void createReviewWithoutItemStatusSold() {
        // given
        User seller = createUser();
        User buyer = createUser();
        userRepository.saveAll(List.of(seller, buyer));

        Item item = createItem(seller, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .content("리뷰내용")
                .score(1.0)
                .build();

        // when // then
        assertThatThrownBy(() -> reviewService.createReview(seller.getId(), buyer.getId(), request, item.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("판매 완료된 상품이 아닙니다.");
    }

    @DisplayName("판매자가 리뷰를 생성할 때, 이미 리뷰가 존재하면 예외가 발생한다.")
    @Test
    void createReviewWithDuplicateSellerReview() {
        // given
        User seller = createUser();
        User buyer = createUser();
        userRepository.saveAll(List.of(seller, buyer));

        Item item = createItem(seller, 10_000, "제목", "내용", SOLD);
        itemRepository.save(item);

        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .content("리뷰내용")
                .score(1.0)
                .build();
        reviewRepository.save(Review.createReview(seller, buyer, item, request, SELLER));

        // when // then
        assertThatThrownBy(() -> reviewService.createReview(seller.getId(), buyer.getId(), request, item.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("이미 리뷰가 존재합니다.");
    }

    @DisplayName("구매자가 리뷰를 생성한다.")
    @Test
    void createReviewByBuyer() {
        // given
        User seller = createUser();
        User buyer = createUser();
        userRepository.saveAll(List.of(seller, buyer));

        Item item = createItem(seller, 10_000, "제목", "내용", SOLD);
        itemRepository.save(item);

        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .content("리뷰내용")
                .score(1.0)
                .build();

        ReviewResponse sellerReview = reviewService.createReview(seller.getId(), buyer.getId(), request, item.getId());

        // when
        ReviewResponse reviewResponse = reviewService.createReview(buyer.getId(), seller.getId(), request, item.getId());

        // then
        assertThat(reviewResponse.getId()).isNotNull();
        assertThat(reviewResponse)
                .extracting("reviewerId", "revieweeId", "content", "reviewerType")
                .containsExactly(buyer.getId(), seller.getId(), request.getContent(), BUYER);
    }

    @DisplayName("구매자가 리뷰를 생성할 때, 판매자의 리뷰가 존재하지 않으면 예외가 발생한다.")
    @Test
    void createReviewWithoutSellerReview() {
        // given
        User seller = createUser();
        User buyer = createUser();
        userRepository.saveAll(List.of(seller, buyer));

        Item item = createItem(seller, 10_000, "제목", "내용", SOLD);
        itemRepository.save(item);

        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .content("리뷰내용")
                .score(1.0)
                .build();

        // when // then
        assertThatThrownBy(() -> reviewService.createReview(buyer.getId(), seller.getId(), request, item.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("구매 내역이 존재하지 않습니다.");
    }

    @DisplayName("구매자가 리뷰를 생성할 때, 이미 리뷰가 존재하면 예외가 발생한다.")
    @Test
    void createReviewWithDuplicateBuyerReview() {
        // given
        User seller = createUser();
        User buyer = createUser();
        userRepository.saveAll(List.of(seller, buyer));

        Item item = createItem(seller, 10_000, "제목", "내용", SOLD);
        itemRepository.save(item);

        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .content("리뷰내용")
                .score(1.0)
                .build();
        reviewRepository.save(Review.createReview(buyer, seller, item, request, SELLER));

        // when // then
        assertThatThrownBy(() -> reviewService.createReview(buyer.getId(), seller.getId(), request, item.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("이미 리뷰가 존재합니다.");
    }

    private User createUser() {
        return User.builder()
                .username("아이디")
                .password("비밀번호")
                .build();
    }

    private Item createItem(final User user, final int price, final String title, final String description, final ItemStatus status) {
        return Item.builder()
                .title(title)
                .description(description)
                .minPriceWanted(price)
                .user(user)
                .status(status)
                .build();
    }
}