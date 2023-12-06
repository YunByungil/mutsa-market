package com.example.market.domain.item;

import com.example.market.domain.user.Coordinate;
import com.example.market.domain.user.User;
import com.example.market.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.market.domain.item.ItemStatus.*;
import static com.example.market.domain.item.ItemStatus.SOLD;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

//    User user;
//
//    @BeforeEach
//    void setUp() {
//        itemRepository.deleteAll();
//        user = userRepository.save(User.builder()
//                .username("아이디")
//                .password("비밀번호")
//                .role(Role.USER)
//                .build());
//    }

    @DisplayName("판매 중인 상품들을 조회한다.")
    @Test
    void findAllByStatus() {
        // given
        final String username = "아이디";
        final String password = "비밀번호";
        User user = createUser(username, password, new Coordinate(37.1, 127.1));
        userRepository.save(user);

        Item item1 = createItem("제목1", "내용1", SALE, 1000, user);
        Item item2 = createItem("제목2", "내용2", SALE, 2000, user);
        Item item3 = createItem("제목3", "내용3", SOLD, 3000, user);

        itemRepository.saveAll(List.of(item1, item2, item3));

        // when
        Page<Item> items = itemRepository.findAllByStatusIn(forDisplay(), PageRequest.of(0, 5));

        // then
        assertThat(items).hasSize(2)
                .extracting("title", "description", "status")
                .containsExactlyInAnyOrder(
                        tuple("제목1", "내용1", SALE),
                        tuple("제목2", "내용2", SALE)
                );
    }

    @DisplayName("내 주변 상품(200KM 이내)만 조회할 수 있다.")
    @Test
    void customFindAllByDistance() {
        // given
        final String username = "아이디";
        final String password = "비밀번호";
        User seller = createUser(username, password, new Coordinate(37.1, 127.1));
        User buyer = createUser(username, password, new Coordinate(37.3, 127.3));
        User sellerWithFar200Km = createUser(username, password, new Coordinate(35.1, 126.1));
        userRepository.saveAll(List.of(seller, buyer, sellerWithFar200Km));

        Item item1 = createItem("제목1", "내용1", SALE, 1000, seller);
        Item item2 = createItem("제목2", "내용2", SALE, 2000, seller);
        Item item3 = createItem("제목3", "내용3", SOLD, 3000, seller);
        Item item4 = createItem("제목4", "내용4", SALE, 3000, sellerWithFar200Km);

        itemRepository.saveAll(List.of(item1, item2, item3, item4));

        // when
        Page<Item> items = itemRepository.customFindAllByDistance(PageRequest.of(0, 5), buyer.getLocation(), buyer.getSearchScope().getScope());

        // then
        assertThat(items).hasSize(3)
                .extracting("title", "description", "status")
                .containsExactlyInAnyOrder(
                        tuple("제목1", "내용1", SALE),
                        tuple("제목2", "내용2", SALE),
                        tuple("제목3", "내용3", SOLD)
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

    private User createUser(final String username, final String password, final Coordinate coordinate) {
        return User.builder()
                .username(username)
                .password(password)
                .location(createPoint(coordinate))
                .build();
    }

    private Point createPoint(final Coordinate coordinate) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(coordinate.getLng(), coordinate.getLat()));
    }
}