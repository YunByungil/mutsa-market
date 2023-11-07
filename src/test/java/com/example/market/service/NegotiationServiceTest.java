//package com.example.market.service;
//
//import com.example.market.domain.entity.Comment;
//import com.example.market.domain.entity.Item;
//import com.example.market.domain.entity.Negotiation;
//import com.example.market.domain.entity.enums.ItemStatus;
//import com.example.market.domain.entity.enums.NegotiationStatus;
//import com.example.market.domain.entity.enums.Role;
//import com.example.market.domain.entity.user.User;
//import com.example.market.dto.negotiation.request.NegotiationCreateRequestDto;
//import com.example.market.dto.negotiation.request.NegotiationUpdateRequestDto;
//import com.example.market.dto.negotiation.response.NegotiationListResponseDto;
//import com.example.market.dto.negotiation.response.NegotiationResponse;
//import com.example.market.exception.MarketAppException;
//import com.example.market.repository.CommentRepository;
//import com.example.market.repository.ItemRepository;
//import com.example.market.repository.NegotiationRepository;
//import com.example.market.repository.user.UserRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ActiveProfiles("test")
//@SpringBootTest
//class NegotiationServiceTest {
//
//    @Autowired
//    NegotiationService negotiationService;
//
//    @Autowired
//    NegotiationRepository negotiationRepository;
//
//    @Autowired
//    ItemRepository itemRepository;
//
//    @Autowired
//    UserRepository userRepository;
//
//    User user;
//    User buyer;
//    User buyer2;
//    Item item;
//
//    @BeforeEach
//    void setUp() {
//        negotiationRepository.deleteAll();
//
//        user = userRepository.save(User.builder()
//                .username("판매자")
//                .password("비밀번호")
//                .role(Role.ADMIN)
//                .build());
//        buyer = userRepository.save(User.builder()
//                .username("구매자")
//                .password("비밀번호")
//                .role(Role.USER)
//                .build());
//        buyer2 = userRepository.save(User.builder()
//                .username("구매자2")
//                .password("비밀번호")
//                .role(Role.USER)
//                .build());
//
//        item = itemRepository.save(Item.builder()
//                .title("제목")
//                .description("설명")
//                .minPriceWanted(10_000)
//                .imageUrl("사진")
//                .user(user)
//                .build());
//    }
//
//    @AfterEach
//    void end() {
//        negotiationRepository.deleteAll();
//    }
//
//    @DisplayName("제안 등록 기능 테스트")
//    @Test
//    void createProposal() {
//        // given
//        final int price = 5_000;
//        NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
//                .suggestedPrice(price)
//                .build();
//
//        // when
//        NegotiationResponse savedNegotiation = negotiationService.createNegotiation(item.getId(), createDto, buyer.getId());
//
//        // then
//        Negotiation negotiation = negotiationRepository.findById(savedNegotiation.getId()).get();
//
//        assertThat(negotiation.getStatus()).isEqualTo(NegotiationStatus.SUGGEST);
//        assertThat(negotiation.getSuggestedPrice()).isEqualTo(price);
//
//    }
//
//    @DisplayName("등록된 제안 판매자 기준 조회 메서드 테스트")
//    @Test
//    void readAllProposalsBySeller() {
//        // given
//        createSixNegotiation();
//
//        int page = 0;
//        int limit = 5;
//
//        // when
//        Page<NegotiationListResponseDto> negotiationListResponseDto = negotiationService.readAllNegotiation(item.getId(), page, limit, user.getId());
//
//        // then
//        assertThat(negotiationListResponseDto.getSize()).isEqualTo(5);
//        assertThat(negotiationListResponseDto.getTotalElements()).isEqualTo(6);
//        assertThat(negotiationListResponseDto.hasNext()).isTrue();
//
//    }
//
//    @DisplayName("등록된 제안 제안자 기준 조회 메서드")
//    @Test
//    void readAllProposalsByNegotiator() {
//        // given
//        createSixNegotiation();
//        createSixNegotiation2();
//        createSixNegotiation2();
//
//        int page = 0;
//        int limit = 5;
//
//        // when
//        Page<NegotiationListResponseDto> negotiationListResponseDto = negotiationService.readAllNegotiation(item.getId(), page, limit, buyer.getId());
//
//        // then
//        assertThat(negotiationListResponseDto.getSize()).isEqualTo(5);
//        assertThat(negotiationListResponseDto.getTotalElements()).isEqualTo(6);
//        assertThat(negotiationListResponseDto.hasNext()).isTrue();
//    }
//
//    @DisplayName("등록된 제안 수정 테스트 (제안자 기준)")
//    @Test
//    void updateNegotiation() {
//        // given
//        final int updatePrice = 10_000;
//        final int price = 5_000;
//        Negotiation negotiation = createNegotiationOne(price, buyer);
//
//        NegotiationUpdateRequestDto updateDto = NegotiationUpdateRequestDto.builder()
//                .suggestedPrice(updatePrice)
//                .build();
//
//        // when
//        negotiationService.updateNegotiation(item.getId(), negotiation.getId(), updateDto, buyer.getId());
//
//        // then
//        Negotiation findNegotiation = negotiationRepository.findById(negotiation.getId()).get();
//        assertThat(findNegotiation.getSuggestedPrice()).isEqualTo(updatePrice);
//    }
//
//    @DisplayName("등록된 제안 수정 *예외* 테스트 (제안자 기준)")
//    @Test
//    void updateNegotiationException() {
//        // given
//        final int updatePrice = 10_000;
//        final int price = 5_000;
//        Negotiation negotiation = createNegotiationOne(price, buyer);
//
//        NegotiationUpdateRequestDto updateDto = NegotiationUpdateRequestDto.builder()
//                .suggestedPrice(updatePrice)
//                .build();
//        // when
//        assertThatThrownBy(() -> {
//            negotiationService.updateNegotiation(item.getId(), negotiation.getId(), updateDto, buyer2.getId());
//        }).isInstanceOf(ResponseStatusException.class);
//
//        // then
//    }
//
//    @DisplayName("등록된 제안 삭제 기능 테스트")
//    @Test
//    void deleteNegotiation() {
//        // given
//        final int price = 10_000;
//        Negotiation negotiation = createNegotiationOne(price, buyer);
//
//        // when
//        negotiationService.deleteNegotiation(item.getId(), negotiation.getId(), buyer.getId());
//
//        // then
//        List<Negotiation> all = negotiationRepository.findAll();
//
//        assertThat(all.size()).isEqualTo(0);
//
//    }
//
//    @DisplayName("등록된 제안 삭제할 때 계정 정보 틀렸을 때 예외 발생")
//    @Test
//    void deleteNegotiationException() {
//        // given
//        final int price = 10_000;
//        Negotiation negotiation = createNegotiationOne(price, buyer);
//
//        // when
//        assertThatThrownBy(() -> {
//            negotiationService.deleteNegotiation(item.getId(), negotiation.getId(), buyer2.getId());
//        }).isInstanceOf(MarketAppException.class);
//
//        // then
//
//    }
//
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
//
//    }
//
//    private Negotiation createNegotiationOne(int price, User buyer) {
//        NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
//                .suggestedPrice(price)
//                .build();
//        Negotiation negotiation = createDto.toEntity(item, buyer, item.getUser());
//
//        negotiationRepository.save(negotiation);
//
//        return negotiation;
//    }
//
//    private void createSixNegotiation() {
//        NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
//                .suggestedPrice(5_000)
//                .build();
//
//        for (int i = 0; i < 6; i++) {
//            negotiationService.createNegotiation(item.getId(), createDto, buyer.getId());
//        }
//    }
//    private void createSixNegotiation2() {
//        NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
//                .suggestedPrice(5_000)
//                .build();
//
//        for (int i = 0; i < 6; i++) {
//            negotiationService.createNegotiation(item.getId(), createDto, buyer2.getId());
//        }
//    }
//}