package com.example.market.service.item;

import com.example.market.IntegrationTestSupport;
import com.example.market.api.controller.item.request.ItemStatusUpdateRequest;
import com.example.market.domain.item.Item;
import com.example.market.domain.item.ItemStatus;
import com.example.market.domain.user.User;
import com.example.market.api.controller.item.request.ItemCreateRequestDto;
import com.example.market.api.controller.item.request.ItemUpdateRequestDto;
import com.example.market.api.controller.item.response.ItemResponse;
import com.example.market.exception.MarketAppException;
import com.example.market.domain.item.ItemRepository;
import com.example.market.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.example.market.domain.item.ItemStatus.*;
import static com.example.market.domain.item.ItemStatus.SALE;
import static org.assertj.core.api.Assertions.*;

class ItemServiceTest extends IntegrationTestSupport {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void end() {
        itemRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("로그인을 한 회원이 상품을 등록한다.")
    @Test
    void createItem() {
        // given
        User user = createUser();
        userRepository.save(user);

        ItemCreateRequestDto request = ItemCreateRequestDto.builder()
                .title("제목1")
                .description("내용1")
                .minPriceWanted(10_000)
                .build();

        // when
        ItemResponse itemResponse = itemService.create(request, user.getId());

        // then
        assertThat(itemResponse.getId()).isNotNull();
        assertThat(itemResponse)
                .extracting("title", "description", "minPriceWanted")
                .contains(request.getTitle(), request.getDescription(), request.getMinPriceWanted());
    }

    @DisplayName("상품을 등록할 때, 존재하지 않은 회원일 경우 예외 발생한다.")
    @Test
    void createItemWithNoUser() {
        // given
        final Long NoExistUser = 0L;

        ItemCreateRequestDto request = ItemCreateRequestDto.builder()
                .title("제목1")
                .description("내용1")
                .minPriceWanted(10_000)
                .build();

        // when // then
        assertThatThrownBy(() -> itemService.create(request, NoExistUser))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @DisplayName("등록된 아이템을 전체 조회(페이징)한다. - 판매 중, 판매 완료 모두 다 조회한다.")
    @Test
    void readItemList() {
        // given
        User user = createUser();
        userRepository.save(user);

        Item item1 = createItem(user, 10_000, "제목1", "내용1", SALE);
        Item item2 = createItem(user, 20_000, "제목2", "내용2", SALE);
        Item item3 = createItem(user, 30_000, "제목3", "내용3", SALE);
        Item item4 = createItem(user, 40_000, "제목4", "내용4", SOLD);
        itemRepository.saveAll(List.of(item1, item2, item3, item4));

        // when
        Page<ItemResponse> itemResponses = itemService.readItemList(0, 20);

        // then
        assertThat(itemResponses).hasSize(4)
                .extracting("title", "description", "minPriceWanted")
                .containsExactlyInAnyOrder(
                        tuple(item1.getTitle(), item1.getDescription(), item1.getMinPriceWanted()),
                        tuple(item2.getTitle(), item2.getDescription(), item2.getMinPriceWanted()),
                        tuple(item3.getTitle(), item3.getDescription(), item3.getMinPriceWanted()),
                        tuple(item4.getTitle(), item4.getDescription(), item4.getMinPriceWanted())
                );
    }

    @DisplayName("등록된 아이템을 전체 조회할 때, 등록된 아이템이 없으면 size값은 0이다.")
    @Test
    void readItemListWithNoItem() {
        // when
        Page<ItemResponse> itemResponses = itemService.readItemList(1, 20);

        // then
        assertThat(itemResponses).hasSize(0);
    }

    @DisplayName("등록된 아이템을 단일 조회한다.")
    @Test
    void readItemOne() {
        // given
        User user = createUser();
        userRepository.save(user);

        ItemCreateRequestDto request = ItemCreateRequestDto.builder()
                .title("제목1")
                .description("내용1")
                .minPriceWanted(10_000)
                .build();
        Item item = itemRepository.save(request.toEntity(user));

        // when
        ItemResponse itemResponse = itemService.readItemOne(item.getId());

        // then
        assertThat(itemResponse.getId()).isNotNull();
        assertThat(itemResponse)
                .extracting("title", "description", "minPriceWanted", "status")
                .contains(request.getTitle(), request.getDescription(), request.getMinPriceWanted(), request.getStatus());
    }

    @DisplayName("아이템을 단일 조회할 때, 존재하지 않는 아이템이면 예외가 발생한다.")
    @Test
    void readItemOneWithNoItem() {
        // given
        final Long NoExistItem = 0L;

        // when // then
        assertThatThrownBy(() -> itemService.readItemOne(NoExistItem))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("존재하지 않는 아이템입니다.");
    }

    @DisplayName("등록된 아이템을 수정한다. 이미지는 포함되지 않는다.")
    @Test
    void updateItemWithNoImage() {
        // given
        User user = createUser();
        userRepository.save(user);

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        ItemUpdateRequestDto request = ItemUpdateRequestDto.builder()
                .title("수정된 제목")
                .minPriceWanted(20_000)
                .description("수정된 내용")
                .build();

        // when
        ItemResponse itemResponse = itemService.updateItem(item.getId(), request, user.getId());

        // then
        assertThat(itemResponse.getId()).isNotNull();
        assertThat(itemResponse)
                .extracting("title", "description", "minPriceWanted")
                .contains(request.getTitle(), request.getDescription(), request.getMinPriceWanted());
    }

    @DisplayName("등록된 아이템을 수정할 때, 본인이 등록한 아이템이 아닐 때 예외가 발생한다.")
    @Test
    void updateItemWithNotEqualWriter() {
        // given
        User user = createUser();
        User anotherUser = createUser();
        userRepository.saveAll(List.of(user, anotherUser));

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        ItemUpdateRequestDto request = ItemUpdateRequestDto.builder()
                .title("수정된 제목")
                .minPriceWanted(20_000)
                .description("수정된 내용")
                .build();

        // when // then
        assertThatThrownBy(() -> itemService.updateItem(item.getId(), request, anotherUser.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("작성자 정보가 일치하지 않습니다.");
    }

    @DisplayName("등록된 아이템을 수정할 때, 존재하지 않는 아이템이면 예외가 발생한다.")
    @Test
    void updateItemWithNoItem() {
        // given
        final Long NoExistItem = 0L;

        User user = createUser();
        userRepository.save(user);

        ItemUpdateRequestDto request = ItemUpdateRequestDto.builder()
                .title("수정된 제목")
                .minPriceWanted(20_000)
                .description("수정된 내용")
                .build();

        // when // then
        assertThatThrownBy(() -> itemService.updateItem(NoExistItem, request, user.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("존재하지 않는 아이템입니다.");
    }

    @DisplayName("등록된 아이템을 수정할 때, 존재하지 않는 회원이면 예외가 발생한다.")
    @Test
    void updateItemWithNoUser() {
        // given
        User user = createUser();
        userRepository.save(user);

        final Long userId = 0L;

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        ItemUpdateRequestDto request = ItemUpdateRequestDto.builder()
                .title("수정된 제목")
                .minPriceWanted(20_000)
                .description("수정된 내용")
                .build();

        // when // then
        assertThatThrownBy(() -> itemService.updateItem(item.getId(), request, userId))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @DisplayName("등록된 아이템을 삭제한다.")
    @Test
    void deleteItem() {
        // given
        User user = createUser();
        userRepository.save(user);

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        // when
        ItemResponse itemResponse = itemService.deleteItem(item.getId(), user.getId());

        // then
        assertThatThrownBy(() -> itemService.readItemOne(item.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("존재하지 않는 아이템입니다.");
    }

    @DisplayName("등록된 아이템을 삭제할 때, 존재하지 않는 아이템이면 예외가 발생한다.")
    @Test
    void deleteItemWithNoItem() {
        // given
        User user = createUser();
        userRepository.save(user);

        final Long itemId = 0L;

        // when // then
        assertThatThrownBy(() -> itemService.deleteItem(itemId, user.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("존재하지 않는 아이템입니다.");
    }

    @DisplayName("등록된 아이템을 삭제할 때, 존재하지 않는 회원이면 예외가 발생한다.")
    @Test
    void deleteItemWithNoUser() {
        // given
        User user = createUser();
        userRepository.save(user);

        final Long userId = 0L;

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        // when // then
        assertThatThrownBy(() -> itemService.deleteItem(item.getId(), userId))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @DisplayName("등록된 아이템을 삭제할 때, 본인이 등록한 아이템이 아니면 예외가 발생한다.")
    @Test
    void deleteItemWithNotEqualWriter() {
        // given
        User user = createUser();
        User anotherUser = createUser();
        userRepository.saveAll(List.of(user, anotherUser));

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        // when // then
        assertThatThrownBy(() -> itemService.deleteItem(item.getId(), anotherUser.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("작성자 정보가 일치하지 않습니다.");
    }

    @DisplayName("등록된 아이템 판매 상태를 수정한다.")
    @Test
    void updateItemStatus() {
        // given
        User buyer = createUser();
        userRepository.save(buyer);

        Item item = createItem(buyer, 10_000, "제목", "설명", SALE);
        itemRepository.save(item);

        ItemStatusUpdateRequest request = ItemStatusUpdateRequest.builder()
                .status(SOLD)
                .build();
        // when
        ItemResponse itemResponse = itemService.updateItemStatus(item.getId(), request, buyer.getId());

        // then
        assertThat(itemResponse.getId()).isNotNull();
        assertThat(itemResponse)
                .extracting("status")
                .isEqualTo(request.getStatus());
    }

    @DisplayName("등록된 아이템 판매 상태를 수정할 때, 작성자가 다르면 예외가 발생한다.")
    @Test
    void updateItemStatusWithNotEqualWriter() {
        // given
        User buyer = createUser();
        User seller = createUser();
        userRepository.saveAll(List.of(buyer, seller));

        Item item = createItem(buyer, 10_000, "제목", "설명", SALE);
        itemRepository.save(item);

        ItemStatusUpdateRequest request = ItemStatusUpdateRequest.builder()
                .status(SOLD)
                .build();

        // when // then
        assertThatThrownBy(() -> itemService.updateItemStatus(item.getId(), request, seller.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("작성자 정보가 일치하지 않습니다.");
    }

    @DisplayName("내가 등록한 상품들 중, 판매 중, 예약 중인 상품들을 조회한다.")
    @Test
    void readMyItemListForSale() {
        // given
        User user = createUser();
        userRepository.save(user);

        Item item1 = createItem(user, 10_000, "제목1", "내용1", SALE);
        Item item2 = createItem(user, 20_000, "제목2", "내용2", SALE);
        Item item3 = createItem(user, 30_000, "제목3", "내용3", SALE);
        Item item4 = createItem(user, 40_000, "제목4", "내용4", SOLD);
        Item item5 = createItem(user, 50_000, "제목5", "내용5", RESERVATION);
        itemRepository.saveAll(List.of(item1, item2, item3, item4, item5));

        // when
        Page<ItemResponse> itemResponses = itemService.readMyItemListForSale(user.getId(), 0);

        // then
        assertThat(itemResponses).hasSize(4)
                .extracting("title", "description", "minPriceWanted")
                .containsExactlyInAnyOrder(
                        tuple(item1.getTitle(), item1.getDescription(), item1.getMinPriceWanted()),
                        tuple(item2.getTitle(), item2.getDescription(), item2.getMinPriceWanted()),
                        tuple(item3.getTitle(), item3.getDescription(), item3.getMinPriceWanted()),
                        tuple(item5.getTitle(), item5.getDescription(), item5.getMinPriceWanted())
                );

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
                .status(SALE)
                .user(user)
                .status(status)
                .build();
    }

}