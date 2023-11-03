package com.example.market.repository;

import com.example.market.domain.entity.Item;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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

    @DisplayName("itemRepository.save() 기능 테스트")
    @Test
    void saveItem() {
        // given
        ItemCreateRequestDto dto = ItemCreateRequestDto.builder()
                .title("제목1")
                .description("내용1")
                .minPriceWanted(10_000)
                .build();

        // when
        Item item = itemRepository.save(dto.toEntity(user));

        // then
        assertThat(item.getTitle()).isEqualTo(dto.getTitle());

    }
}