package com.example.market.repository;

import com.example.market.domain.entity.Comment;
import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.enums.Role;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.comment.request.CommentCreateRequestDto;
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
@Transactional
@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    Item item;

    User user;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();

        user = userRepository.save(User.builder()
                .username("아이디")
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
        commentRepository.deleteAll();
    }

    @DisplayName("CommentRepository 생성 및 조회 테스트")
    @Test
    void createAndFindTest() {
        // given
        Comment comment = commentRepository.save(Comment.builder()
                .item(item)
                .user(user)
                .content("댓글내용")
                .reply("답변대기중")
                .build());

        // when
        List<Comment> all = commentRepository.findAll();

        // then
        assertThat(all.get(0).getContent()).isEqualTo(comment.getContent());
    }

    @DisplayName("findAllByItemId() 메소드 테스트")
    @Test
    void testFindAllByItemId() {
        // given
        CommentCreateRequestDto dto = CommentCreateRequestDto.builder()
                .content("하잉")
                .build();

        Comment comment = null;
        for (int i = 0; i < 20; i++) {
            comment = commentRepository.save(dto.toEntity(item, user));
        }

        Pageable pageable = PageRequest.of(0, 5);

        // when
        Page<Comment> findCommentByAllItemId = commentRepository.findAllByItemId(comment.getItem().getId(), pageable);

        // then
        assertThat(findCommentByAllItemId.hasNext()).isTrue();
        assertThat(findCommentByAllItemId.getTotalElements()).isEqualTo(20L); // 전체 데이터 수
        assertThat(findCommentByAllItemId.getSize()).isEqualTo(5);

    }

}