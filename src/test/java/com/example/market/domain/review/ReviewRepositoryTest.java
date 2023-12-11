package com.example.market.domain.review;

import com.example.market.api.controller.review.request.ReviewCreateRequest;
import com.example.market.domain.item.Item;
import com.example.market.domain.item.ItemRepository;
import com.example.market.domain.item.ItemStatus;
import com.example.market.domain.user.User;
import com.example.market.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.market.domain.item.ItemStatus.SOLD;
import static com.example.market.domain.review.ReviewerType.SELLER;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class ReviewRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @DisplayName("아이템, 리뷰어, 리뷰이를 이용하여 리뷰가 존재하는지 확인한다.")
    @Test
    void existsByItemIdAndReviewerIdAndRevieweeId() {
        // given
        User seller = createUser();
        User buyer = createUser();
        userRepository.saveAll(List.of(seller, buyer));

        Item item = createItem(seller, 10_000, "제목", "내용", SOLD);
        itemRepository.save(item);

        Review review = createReview(seller, buyer, item);
        reviewRepository.save(review);

        // when
        boolean result = reviewRepository.existsByItemIdAndReviewerIdAndRevieweeId(item.getId(), seller.getId(), buyer.getId());

        // then
        assertThat(result).isTrue();
    }

    private Review createReview(final User seller, final User buyer, final Item item) {
        return Review.builder()
                .reviewer(seller)
                .reviewee(buyer)
                .reviewerType(SELLER)
                .content("리뷰내용")
                .score(1.0)
                .item(item)
                .build();
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