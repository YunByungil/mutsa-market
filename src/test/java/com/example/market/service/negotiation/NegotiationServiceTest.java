package com.example.market.service.negotiation;

import com.example.market.IntegrationTestSupport;
import com.example.market.domain.item.Item;
import com.example.market.domain.negotiation.Negotiation;
import com.example.market.domain.item.ItemStatus;
import com.example.market.domain.negotiation.NegotiationStatus;
import com.example.market.domain.user.User;
import com.example.market.api.controller.negotiation.request.NegotiationCreateRequestDto;
import com.example.market.api.controller.negotiation.response.NegotiationResponse;
import com.example.market.exception.MarketAppException;
import com.example.market.domain.item.ItemRepository;
import com.example.market.domain.negotiation.NegotiationRepository;
import com.example.market.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.example.market.domain.item.ItemStatus.SALE;
import static com.example.market.domain.item.ItemStatus.SOLD;
import static com.example.market.domain.negotiation.NegotiationStatus.SUGGEST;
import static org.assertj.core.api.Assertions.*;

class NegotiationServiceTest extends IntegrationTestSupport {

    @Autowired
    private NegotiationService negotiationService;

    @Autowired
    private NegotiationRepository negotiationRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        negotiationRepository.deleteAllInBatch();
        itemRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("등록된 아이템에 가격 제안을 등록한다.")
    @Test
    void createProposal() {
        // given
        User seller = createUser();
        User buyer = createUser();
        userRepository.saveAll(List.of(seller, buyer));

        Item item = createItem(seller, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        NegotiationCreateRequestDto request = NegotiationCreateRequestDto.builder()
                .status(SUGGEST)
                .suggestedPrice(5_000)
                .build();

        // when
        NegotiationResponse negotiation = negotiationService.createNegotiation(item.getId(), request, buyer.getId());

        // then
        assertThat(negotiation.getId()).isNotNull();
        assertThat(negotiation)
                .extracting("status", "suggestedPrice")
                .contains(SUGGEST, request.getSuggestedPrice());
    }

    @DisplayName("등록된 아이템에 가격 제안을 등록할 때, 이미 제안이 등록되어있으면 예외가 발생한다.")
    @Test
    void createProposalWithDuplicateProposal() {
        // given
        User seller = createUser();
        User buyer = createUser();
        userRepository.saveAll(List.of(seller, buyer));

        Item item = createItem(seller, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        NegotiationCreateRequestDto request = NegotiationCreateRequestDto.builder()
                .status(SUGGEST)
                .suggestedPrice(5_000)
                .build();

        negotiationRepository.save(request.toEntity(item, buyer, seller));

        // when // then
        assertThatThrownBy(() -> negotiationService.createNegotiation(item.getId(), request, buyer.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("이미 제안을 요청했습니다.");
    }

    @DisplayName("등록된 아이템에 가격 제안을 등록할 때, 판매된 상품이면 예외가 발생한다.")
    @Test
    void createProposalWithItemStatusSOLD() {
        // given
        User seller = createUser();
        User buyer = createUser();
        userRepository.saveAll(List.of(seller, buyer));

        Item item = createItem(seller, 10_000, "제목", "내용", SOLD);
        itemRepository.save(item);

        NegotiationCreateRequestDto request = NegotiationCreateRequestDto.builder()
                .status(SUGGEST)
                .suggestedPrice(5_000)
                .build();

        // when // then
        assertThatThrownBy(() -> negotiationService.createNegotiation(item.getId(), request, buyer.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("이미 판매된 상품입니다.");
    }

    @DisplayName("등록된 아이템에 가격 제안을 등록할 때, 본인이 작성한 아이템이면 예외가 발생한다.")
    @Test
    void createProposalWithEqualSeller() {
        // given
        User seller = createUser();
        userRepository.save(seller);

        Item item = createItem(seller, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        NegotiationCreateRequestDto request = NegotiationCreateRequestDto.builder()
                .status(SUGGEST)
                .suggestedPrice(5_000)
                .build();

        // when // then
        assertThatThrownBy(() -> negotiationService.createNegotiation(item.getId(), request, seller.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("본인 상품에는 제안을 할 수 없습니다.");
    }

    @DisplayName("가격 제안 받은 제안 목록들을 불러온다.")
    @Test
    void getReceivedNegotiationItems() {
        // given
        User seller = createUser();
        User buyer1 = createUser();
        User buyer2 = createUser();
        userRepository.saveAll(List.of(seller, buyer1, buyer2));

        Item item = createItem(seller, 10_000, "제목1", "내용", SALE);
        itemRepository.save(item);

        Negotiation negotiation1 = createNegotiation(seller, buyer1, item, 7_000);
        Negotiation negotiation2 = createNegotiation(seller, buyer2, item, 8_000);
        negotiationRepository.saveAll(List.of(negotiation1, negotiation2));

        // when
        Page<NegotiationResponse> negotiations = negotiationService.getReceivedNegotiationItems(seller.getId(), 0);

        // then
        assertThat(negotiations).hasSize(2)
                .extracting("itemId", "suggestedPrice")
                .containsExactlyInAnyOrder(
                        tuple(item.getId(), 7_000),
                        tuple(item.getId(), 8_000)
                );
    }

    @DisplayName("가격 제안 한 제안 목록들을 불러온다.")
    @Test
    void getSentNegotiationItems() {
        // given
        User seller = createUser();
        User buyer = createUser();
        userRepository.saveAll(List.of(seller, buyer));

        Item item1 = createItem(seller, 10_000, "제목1", "내용", SALE);
        Item item2 = createItem(seller, 20_000, "제목2", "내용", SALE);
        Item item3 = createItem(seller, 30_000, "제목3", "내용", SALE);
        itemRepository.saveAll(List.of(item1, item2, item3));

        Negotiation negotiation1 = createNegotiation(seller, buyer, item1, 7_000);
        Negotiation negotiation2 = createNegotiation(seller, buyer, item2, 8_000);
        Negotiation negotiation3 = createNegotiation(seller, buyer, item3, 9_000);
        negotiationRepository.saveAll(List.of(negotiation1, negotiation2, negotiation3));

        // when
        Page<NegotiationResponse> negotiations = negotiationService.getSentNegotiationItems(buyer.getId(), 0);

        // then
        assertThat(negotiations).hasSize(3)
                .extracting("itemId", "suggestedPrice")
                .containsExactlyInAnyOrder(
                        tuple(item1.getId(), 7_000),
                        tuple(item2.getId(), 8_000),
                        tuple(item3.getId(), 9_000)
                );
    }

//    @DisplayName("제안의 상태를 변경(제안 -> 수락)하는 메서드 테스트")
//    @Test
//    void updateNegotiationStatus() {
//        // given
//        final int price = 5_000;
//        Negotiation negotiation = createNegotiationOne(price, buyer);
//
//        NegotiationUpdateRequestDto statusDto = NegotiationUpdateRequestDto.builder()
//                .status(NegotiationStatus.ACCEPT.getStatus())
//                .build();
//
//        // when
//        negotiationService.updateNegotiation(item.getId(), negotiation.getId(), statusDto, user.getId());
//
//        // then
//        Negotiation findNegotiation = negotiationRepository.findAll().get(0);
//
//        assertThat(findNegotiation.getStatus()).isEqualTo(NegotiationStatus.ACCEPT);
//
//    }
//
//    @DisplayName("제안의 상태를 변경할 때 계정 정보 다르면 예외 발생 테스트")
//    @Test
//    void updateNegotiationStatusException() {
//        // given
//        final int price = 5_000;
//        Negotiation negotiation = createNegotiationOne(price, buyer);
//
//        NegotiationUpdateRequestDto statusDto = NegotiationUpdateRequestDto.builder()
//                .status(NegotiationStatus.ACCEPT.getStatus())
//                .build();
//
//        // when
//        assertThatThrownBy(() -> {
//            negotiationService.updateNegotiation(item.getId(), negotiation.getId(), statusDto, buyer.getId());
//        }).isInstanceOf(ResponseStatusException.class);
//
//        // then
//
//    }
//
//    @DisplayName("자신이 등록한 제안이 수락 상태일 경우 구매 확정 테스트")
//    @Test
//    void changeStatusAccept() {
//        // given
//        final int price = 5_000;
//        Negotiation negotiation = createNegotiationOne(price, buyer);
//
//        NegotiationUpdateRequestDto statusDto = NegotiationUpdateRequestDto.builder()
//                .status(NegotiationStatus.ACCEPT.getStatus())
//                .build();
//
//        negotiationService.updateNegotiation(item.getId(), negotiation.getId(), statusDto, user.getId());
//
//        NegotiationUpdateRequestDto purchaseDto = NegotiationUpdateRequestDto.builder()
//                .status(NegotiationStatus.CONFIRM.getStatus())
//                .build();
//
//        // when
//        negotiationService.updateNegotiation(item.getId(), negotiation.getId(), purchaseDto, buyer.getId());
//
//        // then
//        Negotiation status = negotiationRepository.findById(negotiation.getId()).get();
//
//        assertThat(status.getStatus()).isEqualTo(NegotiationStatus.CONFIRM);
//
//    }
//    @DisplayName("자신이 등록한 제안이 수락 상태가 아닌 상태에서 확정 요청시 예외 발생")
//    @Test
//    void changeStatusAcceptException() {
//        // given
//        final int price = 5_000;
//        Negotiation negotiation = createNegotiationOne(price, buyer);
//
//        NegotiationUpdateRequestDto purchaseDto = NegotiationUpdateRequestDto.builder()
//                .status("확정")
//                .build();
//
//        // when
//        assertThatThrownBy(() -> {
//            negotiationService.updateNegotiation(item.getId(), negotiation.getId(), purchaseDto, buyer.getId());
//        }).isInstanceOf(ResponseStatusException.class);
//
//        // then
//    }
//
//    @DisplayName("구매 확정시 다른 제안은 거절로 변경")
//    @Test
//    void changeProposalToReject() {
//        // given
//        final int price = 5_000;
//        Negotiation negotiation = createNegotiationOne(price, buyer);
//        Negotiation otherNegotiation = createNegotiationOne(price, buyer2);
//
//        NegotiationUpdateRequestDto statusDto = NegotiationUpdateRequestDto.builder()
//                .status("수락")
//                .build();
//
//        negotiationService.updateNegotiation(item.getId(), negotiation.getId(), statusDto, user.getId());
//
//        NegotiationUpdateRequestDto purchaseDto = NegotiationUpdateRequestDto.builder()
//                .status("확정")
//                .build();
//
//        // when
//        negotiationService.updateNegotiation(item.getId(), negotiation.getId(), purchaseDto, buyer.getId());
//
//        // then
//        Negotiation accept = negotiationRepository.findById(negotiation.getId()).get();
//
//        Negotiation refuse = negotiationRepository.findById(otherNegotiation.getId()).get();
//
//        assertThat(accept.getStatus()).isEqualTo(NegotiationStatus.CONFIRM);
//        assertThat(refuse.getStatus()).isEqualTo(NegotiationStatus.REJECT);
//
//    }
//
//    @DisplayName("구매 확정시 그 아이템의 상태는 판매 완료로 변경")
//    @Test
//    void changeItemStatus() {
//        // given
//        final int price = 5_000;
//        Negotiation negotiation = createNegotiationOne(price, buyer);
//
//        NegotiationUpdateRequestDto statusDto = NegotiationUpdateRequestDto.builder()
//                .status("수락")
//                .build();
//
//        negotiationService.updateNegotiation(item.getId(), negotiation.getId(), statusDto, user.getId());
//
//        NegotiationUpdateRequestDto purchaseDto = NegotiationUpdateRequestDto.builder()
//                .status("확정")
//                .build();
//
//        // when
//        negotiationService.updateNegotiation(item.getId(), negotiation.getId(), purchaseDto, buyer.getId());
//
//        // then
//        Item findItem = itemRepository.findById(item.getId()).get();
//
//        assertThat(findItem.getStatus()).isEqualTo(ItemStatus.SOLD);
//    }

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

    private Negotiation createNegotiation(final User seller, final User buyer, final Item item, final int price) {
        return Negotiation.builder()
                .seller(seller)
                .suggestedPrice(price)
                .buyer(buyer)
                .item(item)
                .status(NegotiationStatus.SUGGEST)
                .build();
    }
}