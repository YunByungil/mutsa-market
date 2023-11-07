package com.example.market.repository;

import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.enums.ItemStatus;
import com.example.market.domain.entity.enums.Role;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.item.request.ItemCreateRequestDto;
import com.example.market.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.market.domain.entity.enums.ItemStatus.*;
import static com.example.market.domain.entity.enums.ItemStatus.SOLD;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        user = userRepository.save(User.builder()
                .username("아이디")
                .password("비밀번호")
                .role(Role.USER)
                .build());
    }

    @DisplayName("판매 중인 상품들을 조회한다.")
    @Test
    void findAllByStatus() {
        // given
        Item item1 = createItem("제목1", "내용1", SALE, 1000, user);
        Item item2 = createItem("제목2", "내용2", SALE, 2000, user);
        Item item3 = createItem("제목3", "내용3", SOLD, 3000, user);

        itemRepository.saveAll(List.of(item1, item2, item3));

        // when
        Page<Item> items = itemRepository.findAllByStatus(forDisplay(), PageRequest.of(0, 5));

        // then
        assertThat(items).hasSize(2)
                .extracting("title", "description", "status")
                .containsExactlyInAnyOrder(
                        tuple("제목1", "내용1", SALE),
                        tuple("제목2", "내용2", SALE)
                );
    }

    private Item createItem(final String title, final String description, final ItemStatus status, final int price, final User user) {
        return Item.builder()
                .title(title)
                .description(description)
                .status(status)
                .minPriceWanted(price)
                .user(user)
                .build();
    }
}