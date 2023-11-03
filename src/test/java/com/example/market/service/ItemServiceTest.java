package com.example.market.service;

import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.enums.Role;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.item.request.ItemCreateRequestDto;
import com.example.market.dto.item.request.ItemUpdateRequestDto;
import com.example.market.dto.item.response.ItemListResponseDto;
import com.example.market.dto.item.response.ItemOneResponseDto;
import com.example.market.exception.MarketAppException;
import com.example.market.repository.ItemRepository;
import com.example.market.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    User user;
    User anotherUser;
    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        user = userRepository.save(User.builder()
                .username("아이디")
                .password("비밀번호")
                .role(Role.USER)
                .build());
        anotherUser = userRepository.save(User.builder()
                .username("다른아이디")
                .password("다른비밀번호")
                .role(Role.USER)
                .build());
    }

    @AfterEach
    void end() {
        itemRepository.deleteAll();
    }

    @DisplayName("아이템 등록 테스트")
    @Test
    void createItem() {

        // given
        ItemCreateRequestDto dto = ItemCreateRequestDto.builder()
                .title("제목1")
                .description("내용1")
                .minPriceWanted(10_000)
                .build();

        itemService.create(dto, user.getId());

        // when
        List<Item> all = itemRepository.findAll();

        // then
        assertThat(all.size()).isEqualTo(1);

    }

    @DisplayName("아이템 전체 조회(페이징) 테스트")
    @Test
    void readItemList() {
        // given
        Integer page = 0;
        Integer limit = 20;
        createItems();

        // when
        Page<ItemListResponseDto> itemListDto = itemService.readItemList(page, limit);

        // then
        assertThat(itemListDto.getSize()).isEqualTo(20);

    }

    @DisplayName("아이템 단일 조회 테스트")
    @Test
    void readItemOne() {
        // given
        ItemCreateRequestDto dto = ItemCreateRequestDto.builder()
                .title("제목1")
                .description("내용1")
                .minPriceWanted(10_000)
                .build();

        Item save = itemRepository.save(dto.toEntity(user));

        // when
        ItemOneResponseDto itemOneResponseDto = itemService.readItemOne(save.getId());

        // then
        assertThat(itemOneResponseDto.getTitle()).isEqualTo(dto.getTitle());
    }

    @DisplayName("존재하지 않는 아이템일 때 예외 발생")
    @Test
    void notItemException() {
        assertThatThrownBy(() -> {
            itemService.readItemOne(1L);
        }).isInstanceOf(MarketAppException.class);
    }

    @DisplayName("아이템 수정 테스트(이미지X)")
    @Test
    void updateItem() {
        // given
        ItemCreateRequestDto dto = ItemCreateRequestDto.builder()
                .title("새로운 제목")
                .minPriceWanted(10_000)
                .description("새로운 내용")
                .build();

        Item savedItem = itemRepository.save(dto.toEntity(user));

        ItemUpdateRequestDto updateDto = ItemUpdateRequestDto.builder()
                .title("수정된 제목")
                .minPriceWanted(20_000)
                .description("수정된 내용")
                .build();


        // when
        itemService.updateItem(savedItem.getId(), updateDto, user.getId());
        Item updateItem = itemRepository.findById(savedItem.getId()).get();

        // then
        assertThat(updateItem.getTitle()).isEqualTo(updateDto.getTitle());
    }

    @DisplayName("아이템 수정 Writer or Password 틀렸을 때 예외 발생")
    @Test
    void updateItemDontMatchWriterOrPassword() {
        // given
        ItemCreateRequestDto dto = ItemCreateRequestDto.builder()
                .title("새로운 제목")
                .minPriceWanted(10_000)
                .description("새로운 내용")
                .build();

        Item savedItem = itemRepository.save(dto.toEntity(user));

        ItemUpdateRequestDto notSameWriterUpdateDto = ItemUpdateRequestDto.builder()
                .title("수정된 제목")
                .minPriceWanted(20_000)
                .description("수정된 내용")
                .build();

        // when

        // then
        assertThatThrownBy(() -> {
            itemService.updateItem(savedItem.getId(), notSameWriterUpdateDto, anotherUser.getId());
        }).isInstanceOf(MarketAppException.class);

    }

    @DisplayName("아이템 삭제 기능 테스트")
    @Test
    void deleteItem() {
        // given
        ItemCreateRequestDto dto = ItemCreateRequestDto.builder()
                .title("새로운 제목")
                .minPriceWanted(10_000)
                .description("새로운 내용")
                .build();

        Item savedItem = itemRepository.save(dto.toEntity(user));


        // when
        itemService.deleteItem(savedItem.getId(), user.getId());

        // then
        List<Item> all = itemRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
    }

    private void createItems() {
        for (int i = 1; i <= 30; i++) {
            ItemCreateRequestDto dto = ItemCreateRequestDto.builder()
                    .title("제목" + i)
                    .description("내용" + i)
                    .minPriceWanted(i)
                    .build();

            itemService.create(dto, user.getId());
        }
    }

}