package com.example.market.repository;

import com.example.market.domain.entity.Comment;
import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.Negotiation;
import com.example.market.domain.entity.enums.ItemStatus;
import com.example.market.domain.entity.enums.NegotiationStatus;
import com.example.market.domain.entity.enums.Role;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.comment.request.CommentCreateRequestDto;
import com.example.market.dto.negotiation.request.NegotiationCreateRequestDto;
import com.example.market.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class NegotiationRepositoryTest {

    @Autowired
    private NegotiationRepository negotiationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @DisplayName("가격을 제안 받은 내역을 불러온다. (내가 받은 제안들)")
    @Test
    void findAllBySellerId() {
        // given
        User seller = createSeller("seller");
        User buyer1 = createBuyer("buyer1");
        User buyer2 = createBuyer("buyer2");
        userRepository.saveAll(List.of(seller, buyer1, buyer2));

        Item item = createItem(seller);
        itemRepository.save(item);

        Negotiation negotiation1 = createNegotiation(seller, buyer1, item);
        Negotiation negotiation2 = createNegotiation(seller, buyer2, item);
        negotiationRepository.saveAll(List.of(negotiation1, negotiation2));

        // when
        Page<Negotiation> negotiations = negotiationRepository.findAllBySellerId(seller.getId(), PageRequest.of(0, 5));

        // then
        assertThat(negotiations).hasSize(2)
                .extracting("item", "seller", "buyer")
                .containsExactlyInAnyOrder(
                        tuple(item, seller, buyer1),
                        tuple(item, seller, buyer2)
                );
    }

    @DisplayName("가격을 제안 보낸 내역을 불러온다. (내가 보낸 제안들)")
    @Test
    void findAllBuyerId() {
        // given
        User seller = createSeller("seller");
        User buyer1 = createBuyer("buyer1");
        userRepository.saveAll(List.of(seller, buyer1));

        Item item1 = createItem(seller);
        Item item2 = createItem(seller);
        Item item3 = createItem(seller);
        itemRepository.saveAll(List.of(item1, item2, item3));

        Negotiation negotiation1 = createNegotiation(seller, buyer1, item1);
        Negotiation negotiation2 = createNegotiation(seller, buyer1, item2);
        Negotiation negotiation3 = createNegotiation(seller, buyer1, item3);
        negotiationRepository.saveAll(List.of(negotiation1, negotiation2, negotiation3));

        // when
        Page<Negotiation> negotiations = negotiationRepository.findAllByBuyerId(buyer1.getId(), PageRequest.of(0, 5));

        // then
        assertThat(negotiations).hasSize(3)
                .extracting("item", "seller", "buyer")
                .containsExactlyInAnyOrder(
                        tuple(item1, seller, buyer1),
                        tuple(item2, seller, buyer1),
                        tuple(item3, seller, buyer1)
                );
    }

//    @DisplayName("findAllByItemId() 메소드 테스트")
//    @Test
//    void testFindAllByItemId() {
//        // given
//        NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
//                .suggestedPrice(10_000)
//                .build();
//
//        Negotiation negotiation = null;
//        for (int i = 0; i < 20; i++) {
//            negotiation = repository.save(createDto.toEntity(item, user));
//        }
//
//        Pageable pageable = PageRequest.of(0, 5);
//
//        // when
//        Page<Negotiation> findNegotiationByAllItemId = repository.findAllByItemId(item.getId(), pageable);
//
//        // then
////        assertThat(findNegotiationByAllItemId.hasNext()).isTrue();
//        assertThat(findNegotiationByAllItemId.getTotalElements()).isEqualTo(20L); // 전체 데이터 수
//        assertThat(findNegotiationByAllItemId.getSize()).isEqualTo(5);
//
//    }
//
//    @DisplayName("findByItemId() 메서드 테스트")
//    @Test
//    void findByItemId() {
//        // given
//        final int price = 12_345;
//        final int price2 = 54_321;
//        NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
//                .suggestedPrice(price)
//                .build();
//        NegotiationCreateRequestDto createDto2 = NegotiationCreateRequestDto.builder()
//                .suggestedPrice(price2)
//                .build();
//
//        repository.save(createDto.toEntity(item, buyer));
//        repository.save(createDto2.toEntity(item, buyer));
//
//        // when
//        Negotiation negotiation = repository.findAll().get(0);
//        Negotiation negotiation2 = repository.findAll().get(1);
//
//        // then
//        assertThat(repository.existsByItemId(item.getId())).isTrue();
//        assertThat(negotiation.getSuggestedPrice()).isEqualTo(price);
//        assertThat(negotiation2.getSuggestedPrice()).isEqualTo(price2);
//    }
//
//    @DisplayName("findAllByItemIdAndUserId() 메서드 테스트")
//    @Test
//    void findAllByItemAndWriterAndPassword() {
//        // given
//        final int page = 0;
//        final int limit = 5;
//        NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
//                .suggestedPrice(10_000)
//                .build();
//
//        Negotiation negotiation = null;
//        for (int i = 0; i < 5; i++) {
//            negotiation = repository.save(createDto.toEntity(item, buyer));
//        }
//
//        NegotiationCreateRequestDto createDto2 = NegotiationCreateRequestDto.builder()
//                .suggestedPrice(10_000)
//                .build();
//
//        Negotiation negotiation2 = null;
//        for (int i = 0; i < 3; i++) {
//            negotiation2 = repository.save(createDto2.toEntity(item, buyer2));
//        }
//
//        Pageable pageable = PageRequest.of(page, limit);
//
//        // when
//        Page<Negotiation> allByItemIdAndWriterAndPassword =
//                repository.findAllByItemIdAndUserId(item.getId(), buyer.getId(), pageable);
//
//        // then
//        assertThat(allByItemIdAndWriterAndPassword.getTotalElements()).isEqualTo(5);
//    }
//
//    @DisplayName("구매가 확정되었을 때 그 구매 제안을 제외한 나머지 제안은 거절로 변경하는 메서드 테스트")
//    @Test
//    void updateNegotiationStatus() {
//        // given
//        Negotiation accept = repository.save(Negotiation.builder()
//                .item(item)
//                .user(buyer)
//                .build());
//
//        Negotiation negotiation = repository.save(Negotiation.builder()
//                .user(buyer2)
//                .item(item)
//                .build());
//
//        // when
//        assertThat(negotiation.getStatus()).isEqualTo(NegotiationStatus.SUGGEST);
//
//        repository.updateNegotiationStatus(accept.getId(), item.getId());
//
//        // then
//        Negotiation refuse = repository.findById(negotiation.getId()).get();
//
//        assertThat(refuse.getStatus()).isEqualTo(NegotiationStatus.REJECT);
//    }

    private User createSeller(final String username) {
        User user = User.builder()
                .username(username)
                .password("password")
                .build();
        return user;
    }

    private User createBuyer(final String username) {
        User user = User.builder()
                .username(username)
                .password("password")
                .build();
        return user;
    }

    private Item createItem(final User seller) {
        Item item = Item.builder()
                .status(ItemStatus.SALE)
                .title("title")
                .description("description")
                .minPriceWanted(5_000)
                .user(seller)
                .build();
        return item;
    }

    private Negotiation createNegotiation(final User seller, final User buyer, final Item item) {
        Negotiation negotiation = Negotiation.builder()
                .seller(seller)
                .buyer(buyer)
                .item(item)
                .status(NegotiationStatus.SUGGEST)
                .build();

        return negotiation;
    }
}