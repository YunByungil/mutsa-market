package com.example.market.domain.item;

import com.example.market.domain.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.market.domain.item.ItemStatus.SALE;
import static com.example.market.domain.item.ItemStatus.SOLD;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @DisplayName("상품 상태를 수정한다.")
    @Test
    void updateStatus() {
        // given
        User user = createUser();

        Item item = createItem(user);

        // when
        item.updateStatus(SOLD);

        // then
        assertThat(item.getStatus()).isEqualTo(SOLD);
    }

    private Item createItem(final User user) {
        return Item.builder()
                .title("title")
                .description("description")
                .minPriceWanted(10_000)
                .status(SALE)
                .user(user)
                .build();
    }

    private User createUser() {
        return User.builder()
                .username("username")
                .password("password")
                .build();
    }

}