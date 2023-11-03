package com.example.market.service;

import com.example.market.domain.entity.Comment;
import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.enums.Role;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.comment.request.CommentCreateRequestDto;
import com.example.market.dto.comment.request.CommentReplyRequestDto;
import com.example.market.dto.comment.request.CommentUpdateRequestDto;
import com.example.market.dto.comment.response.CommentListResponseDto;
import com.example.market.exception.MarketAppException;
import com.example.market.repository.CommentRepository;
import com.example.market.repository.ItemRepository;
import com.example.market.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    User user;
    User anotherUser;
    Item item;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();

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
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @DisplayName("댓글 생성 기능 테스트")
    @Test
    void createComment() {
        // given
        CommentCreateRequestDto dto = CommentCreateRequestDto.builder()
                .content("내용")
                .build();

        commentService.create(item.getId(), dto, user.getId());

        // when
        List<Comment> all = commentRepository.findAll();
        Comment comment = all.get(0);

        // then
        assertThat(comment.getContent()).isEqualTo(dto.getContent());
    }
    
    @DisplayName("존재하지 않는 물품에 댓글 작성시 예외 발생")
    @Test
    void notExistItemException() {
        // given
        Long itemId = 9999L;

        CommentCreateRequestDto dto = CommentCreateRequestDto.builder()
                .content("내용")
                .build();

        // when
        assertThatThrownBy(() -> {
            commentService.create(itemId, dto, user.getId());
        }).isInstanceOf(MarketAppException.class);
        
        // then
        
    }

    @DisplayName("댓글 조회 메서드 테스트")
    @Test
    void readCommentList() {
        // given
        CommentCreateRequestDto dto = CommentCreateRequestDto.builder()
                .content("내용")
                .build();

        for (int i = 0; i < 6; i++) {
            commentService.create(item.getId(), dto, user.getId());
        }

        // when
        Page<CommentListResponseDto> commentListResponseDto = commentService.readCommentList(item.getId(), 0, 5);

        // then
        assertThat(commentListResponseDto.getTotalElements()).isEqualTo(6L);
        assertThat(commentListResponseDto.getSize()).isEqualTo(5);

    }

    @DisplayName("댓글 조회시, 존재하지 않는 경우 테스트")
    @Test
    void notExistComment() {
        // given

        // when
        Page<CommentListResponseDto> commentListResponseDto = commentService.readCommentList(5000L, 0, 5);

        // then
        assertThat(commentListResponseDto.getTotalElements()).isEqualTo(0L);

    }

    @DisplayName("댓글 수정 테스트")
    @Test
    void updateComment() {
        // given
        Long commentId = createCommentMethod();

        CommentUpdateRequestDto dto = CommentUpdateRequestDto.builder()
                .content("수정완료")
                .build();


        // when
        commentService.updateComment(item.getId(), commentId, dto, user.getId());

        // then
        Comment comment = commentRepository.findById(commentId).get();

        assertThat(comment.getContent()).isEqualTo(dto.getContent());

    }

    @DisplayName("댓글 수정할 때 작성자 다를 때 예외 발생")
    @Test
    void checkWriterAndPasswordForUpdate() {
        // given
        Long commentId = createCommentMethod();

        CommentUpdateRequestDto dontMatchWriter = CommentUpdateRequestDto.builder()
                .content("수정완료")
                .build();


        // when
        assertThatThrownBy(() -> { // 1. 작성자가 다른 경우
            commentService.updateComment(item.getId(), commentId, dontMatchWriter, anotherUser.getId());
        }).isInstanceOf(MarketAppException.class);

        // then
    }

    @DisplayName("댓글 삭제 기능 테스트")
    @Test
    void deleteComment() {
        // given
        Long commentId = createCommentMethod();

        // when
        commentService.deleteComment(item.getId(), commentId, user.getId());

        // then
        List<Comment> all = commentRepository.findAll();
        assertThat(all.size()).isEqualTo(0);

    }

    @DisplayName("댓글 삭제할 때 내가 작성한 댓글이 아닐 때 예외 발생")
    @Test
    void checkWriterAndPasswordForDelete() {
        // given
        Long commentId = createCommentMethod();

        // when
        assertThatThrownBy(() -> {
            commentService.deleteComment(item.getId(), commentId, anotherUser.getId());
        }).isInstanceOf(MarketAppException.class);

        // then
    }

    @DisplayName("댓글에 답변 달기 기능 테스트")
    @Test
    void updateCommentReply() {
        // given
        Long commentId = createCommentMethod();

        CommentReplyRequestDto replyDto = CommentReplyRequestDto.builder()
                .reply("답변작성완료")
                .build();

        // when
        commentService.updateCommentReply(item.getId(), commentId, replyDto, user.getId());

        // then
        Comment findComment = commentRepository.findById(commentId).get();
        assertThat(findComment.getReply()).isEqualTo(replyDto.getReply());
    }

    @DisplayName("답변 기능(reply) 글 작성 본인이 아닐 경우 예외 발생")
    @Test
    void checkWriterAndPasswordForReply() {
        // given
        Long commentId = createCommentMethod();

        CommentReplyRequestDto replyDto = CommentReplyRequestDto.builder()
                .reply("답변수정완료")
                .build();

        // when
        assertThatThrownBy(() -> {
            commentService.updateCommentReply(item.getId(), commentId, replyDto, anotherUser.getId());
        }).isInstanceOf(MarketAppException.class);

        // then
    }

    private Long createCommentMethod() {
        CommentCreateRequestDto dto = CommentCreateRequestDto.builder()
                .content("내용")
                .build();

        Comment comment = commentRepository.save(dto.toEntity(item, user));

        return comment.getId();
    }

}