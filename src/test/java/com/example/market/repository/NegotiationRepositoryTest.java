package com.example.market.repository;

import com.example.market.domain.entity.Comment;
import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.Negotiation;
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
    NegotiationRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    User user;
    User buyer;
    User buyer2;
    Item item;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        user = userRepository.save(User.builder()
                .username("판매자")
                .password("비밀번호")
                .role(Role.ADMIN)
                .build());
        buyer = userRepository.save(User.builder()
                .username("구매자")
                .password("비밀번호")
                .role(Role.USER)
                .build());
        buyer2 = userRepository.save(User.builder()
                .username("구매자2")
                .password("비밀번호")
                .role(Role.USER)
                .build());

        item = itemRepository.save(Item.builder()
                .title("제목")
                .description("설명")
                .minPriceWanted(10_000)
                .imageUrl("사진")
                .user(user)
                .build());
    }

    @AfterEach
    void end() {
        repository.deleteAll();
    }

    @DisplayName("Negotiation Repository 생성 및 조회 테스트")
    @Test
    void createAndFind() {
        // given
//        final String defaultStatus = "제안";
        repository.save(Negotiation.builder()
                .item(item)
                .suggestedPrice(10_000)
                .user(buyer)
                .build());

        // when
        List<Negotiation> all = repository.findAll();
        Negotiation negotiation = all.get(0);

        // then
        assertThat(negotiation.getSuggestedPrice()).isEqualTo(10_000);
        assertThat(negotiation.getStatus()).isEqualTo(NegotiationStatus.SUGGEST);
    }

    @DisplayName("findAllByItemId() 메소드 테스트")
    @Test
    void testFindAllByItemId() {
        // given
        NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
                .suggestedPrice(10_000)
                .build();

        Negotiation negotiation = null;
        for (int i = 0; i < 20; i++) {
            negotiation = repository.save(createDto.toEntity(item, user));
        }

        Pageable pageable = PageRequest.of(0, 5);

        // when
        Page<Negotiation> findNegotiationByAllItemId = repository.findAllByItemId(item.getId(), pageable);

        // then
//        assertThat(findNegotiationByAllItemId.hasNext()).isTrue();
        assertThat(findNegotiationByAllItemId.getTotalElements()).isEqualTo(20L); // 전체 데이터 수
        assertThat(findNegotiationByAllItemId.getSize()).isEqualTo(5);

    }

    @DisplayName("findByItemId() 메서드 테스트")
    @Test
    void findByItemId() {
        // given
        final int price = 12_345;
        final int price2 = 54_321;
        NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
                .suggestedPrice(price)
                .build();
        NegotiationCreateRequestDto createDto2 = NegotiationCreateRequestDto.builder()
                .suggestedPrice(price2)
                .build();

        repository.save(createDto.toEntity(item, buyer));
        repository.save(createDto2.toEntity(item, buyer));

        // when
        Negotiation negotiation = repository.findAll().get(0);
        Negotiation negotiation2 = repository.findAll().get(1);

        // then
        assertThat(repository.existsByItemId(item.getId())).isTrue();
        assertThat(negotiation.getSuggestedPrice()).isEqualTo(price);
        assertThat(negotiation2.getSuggestedPrice()).isEqualTo(price2);
    }

    @DisplayName("findAllByItemIdAndUserId() 메서드 테스트")
    @Test
    void findAllByItemAndWriterAndPassword() {
        // given
        final int page = 0;
        final int limit = 5;
        NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
                .suggestedPrice(10_000)
                .build();

        Negotiation negotiation = null;
        for (int i = 0; i < 5; i++) {
            negotiation = repository.save(createDto.toEntity(item, buyer));
        }

        NegotiationCreateRequestDto createDto2 = NegotiationCreateRequestDto.builder()
                .suggestedPrice(10_000)
                .build();

        Negotiation negotiation2 = null;
        for (int i = 0; i < 3; i++) {
            negotiation2 = repository.save(createDto2.toEntity(item, buyer2));
        }

        Pageable pageable = PageRequest.of(page, limit);

        // when
        Page<Negotiation> allByItemIdAndWriterAndPassword =
                repository.findAllByItemIdAndUserId(item.getId(), buyer.getId(), pageable);

        // then
        assertThat(allByItemIdAndWriterAndPassword.getTotalElements()).isEqualTo(5);
    }

    @DisplayName("구매가 확정되었을 때 그 구매 제안을 제외한 나머지 제안은 거절로 변경하는 메서드 테스트")
    @Test
    void updateNegotiationStatus() {
        // given
        Negotiation accept = repository.save(Negotiation.builder()
                .item(item)
                .user(buyer)
                .build());

        Negotiation negotiation = repository.save(Negotiation.builder()
                .user(buyer2)
                .item(item)
                .build());

        // when
        assertThat(negotiation.getStatus()).isEqualTo(NegotiationStatus.SUGGEST);

        repository.updateNegotiationStatus(accept.getId(), item.getId());

        // then
        Negotiation refuse = repository.findById(negotiation.getId()).get();

        assertThat(refuse.getStatus()).isEqualTo(NegotiationStatus.REJECT);
    }
}