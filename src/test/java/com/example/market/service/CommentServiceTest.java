package com.example.market.service;

import com.example.market.IntegrationTestSupport;
import com.example.market.domain.entity.Comment;
import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.enums.ItemStatus;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.comment.request.CommentCreateRequestDto;
import com.example.market.dto.comment.request.CommentReplyRequestDto;
import com.example.market.dto.comment.request.CommentUpdateRequestDto;
import com.example.market.dto.comment.response.CommentResponse;
import com.example.market.exception.MarketAppException;
import com.example.market.repository.CommentRepository;
import com.example.market.repository.ItemRepository;
import com.example.market.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.example.market.domain.entity.enums.ItemStatus.SALE;
import static org.assertj.core.api.Assertions.*;

class CommentServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void end() {
        commentRepository.deleteAllInBatch();
        itemRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("로그인을 한 회원이 댓글을 등록한다.")
    @Test
    void createComment() {
        // given
        User user = createUser();
        userRepository.save(user);

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        CommentCreateRequestDto request = CommentCreateRequestDto.builder()
                .content("내용")
                .build();

        // when
        CommentResponse commentResponse = commentService.create(item.getId(), request, user.getId());

        // then
        assertThat(commentResponse.getId()).isNotNull();
        assertThat(commentResponse)
                .extracting("content", "username")
                .contains(request.getContent(), user.getUsername());
    }
    
    @DisplayName("로그인을 한 회원이 댓글을 작성할 때, 존재하지 않는 아이템이면 예외가 발생한다.")
    @Test
    void createCommentWithNoItem() {
        // given
        final Long noExistItem = 0L;

        User user = createUser();
        userRepository.save(user);

        CommentCreateRequestDto request = CommentCreateRequestDto.builder()
                .content("내용")
                .build();

        // when // then
        assertThatThrownBy(() -> commentService.create(noExistItem, request, user.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("존재하지 않는 아이템입니다.");
    }

    @DisplayName("댓글을 작성할 때, 존재하지 않는 회원이면 예외가 발생한다.")
    @Test
    void createCommentWithNoUser() {
        // given
        final Long noExistUser = 0L;
        User user = createUser();
        userRepository.save(user);

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        CommentCreateRequestDto request = CommentCreateRequestDto.builder()
                .content("내용")
                .build();

        // when // then
        assertThatThrownBy(() -> commentService.create(item.getId(), request, noExistUser))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @DisplayName("댓글 조회 메서드 테스트")
    @Test
    void readCommentList() {
        // given
        User user = createUser();
        userRepository.save(user);

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        Comment comment1 = createComment(user, "내용1", item);
        Comment comment2 = createComment(user, "내용2", item);
        Comment comment3 = createComment(user, "내용3", item);
        commentRepository.saveAll(List.of(comment1, comment2, comment3));

        // when
        Page<CommentResponse> commentResponses = commentService.readCommentList(item.getId(), 0, 5);

        // then
        assertThat(commentResponses).hasSize(3)
                .extracting("content", "itemId")
                .containsExactlyInAnyOrder(
                        tuple("내용1", item.getId()),
                        tuple("내용2", item.getId()),
                        tuple("내용3", item.getId())
                );
    }
    
    @DisplayName("등록된 댓글 조회 시, 등록된 댓글이 없으면 size값은 0이다.")
    @Test
    void readCommentListWithNoComment() {
        final Long noExistItem = 0L;

        // when
        Page<CommentResponse> commentResponses = commentService.readCommentList(noExistItem, 1, 5);

        // then
        assertThat(commentResponses).hasSize(0);
    }

    @DisplayName("등록된 댓글을 수정한다.")
    @Test
    void updateComment() {
        // given
        User user = createUser();
        userRepository.save(user);

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        Comment comment = createComment(user, "내용", item);
        commentRepository.save(comment);

        CommentUpdateRequestDto request = CommentUpdateRequestDto.builder()
                .content("수정완료")
                .build();

        // when
        CommentResponse commentResponse = commentService.updateComment(item.getId(), comment.getId(), request, user.getId());

        // then
        assertThat(commentResponse.getId()).isNotNull();
        assertThat(commentResponse.getContent()).isEqualTo(request.getContent());
    }

    @DisplayName("등록된 댓글을 수정할 때, 존재하지 않는 아이템이면 예외가 발생한다.")
    @Test
    void updateCommentWithNoItem() {
        // given
        final Long noExistItem = 0L;

        User user = createUser();
        userRepository.save(user);

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        Comment comment = createComment(user, "내용", item);
        commentRepository.save(comment);

        CommentUpdateRequestDto request = CommentUpdateRequestDto.builder()
                .content("수정완료")
                .build();

        // when // then
        assertThatThrownBy(() -> commentService.updateComment(noExistItem, comment.getId(), request, user.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("존재하지 않는 아이템입니다.");
    }

    @DisplayName("등록된 댓글을 수정할 때, 존재하지 않는 댓글이면 예외가 발생한다.")
    @Test
    void updateCommentWithNoComment() {
        // given
        final Long noExistComment = 0L;

        User user = createUser();
        userRepository.save(user);

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        Comment comment = createComment(user, "내용", item);
        commentRepository.save(comment);

        CommentUpdateRequestDto request = CommentUpdateRequestDto.builder()
                .content("수정완료")
                .build();

        // when // then
        assertThatThrownBy(() -> commentService.updateComment(item.getId(), noExistComment, request, user.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("존재하지 않는 댓글입니다.");
    }

    @DisplayName("등록된 댓글을 수정할 때, 존재하지 않는 회원이면 예외가 발생한다.")
    @Test
    void updateCommentWithNoUser() {
        // given
        final Long noExistUser = 0L;

        User user = createUser();
        userRepository.save(user);

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        Comment comment = createComment(user, "내용", item);
        commentRepository.save(comment);

        CommentUpdateRequestDto request = CommentUpdateRequestDto.builder()
                .content("수정완료")
                .build();

        // when // then
        assertThatThrownBy(() -> commentService.updateComment(item.getId(), comment.getId(), request, noExistUser))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @DisplayName("등록된 댓글을 수정할 때, 본인이 등록한 댓글이 아니면 예외가 발생한다.")
    @Test
    void updateCommentWithNotEqualUser() {
        // given
        User user = createUser();
        User anotherUser = createUser();
        userRepository.saveAll(List.of(user, anotherUser));

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        Comment comment = createComment(user, "댓글", item);
        commentRepository.save(comment);

        CommentUpdateRequestDto request = CommentUpdateRequestDto.builder()
                .content("수정완료")
                .build();

        // when // then
        assertThatThrownBy(() -> commentService.updateComment(item.getId(), comment.getId(), request, anotherUser.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("작성자 정보가 일치하지 않습니다.");
    }

    @DisplayName("등록된 댓글을 삭제한다.")
    @Test
    void deleteComment() {
        // given
        User user = createUser();
        userRepository.save(user);

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        Comment comment = createComment(user, "내용", item);
        commentRepository.save(comment);

        // when
        CommentResponse commentResponse = commentService.deleteComment(item.getId(), comment.getId(), user.getId());

        // then
        List<Comment> all = commentRepository.findAll();
        assertThat(all).hasSize(0);
    }

    @DisplayName("등록된 댓글을 삭제할 때, 본인이 등록한 댓글이 아니면 예외가 발생한다.")
    @Test
    void deleteCommentWithNotEqualUser() {
        // given
        User user = createUser();
        User anotherUser = createUser();
        userRepository.saveAll(List.of(user, anotherUser));

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        Comment comment = createComment(user, "내용", item);
        commentRepository.save(comment);

        // when // then
        assertThatThrownBy(() -> commentService.deleteComment(item.getId(), comment.getId(), anotherUser.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("작성자 정보가 일치하지 않습니다.");
    }

    @DisplayName("아이템 등록자는 댓글에 답글을 작성할 수 있다.")
    @Test
    void updateCommentReply() {
        // given
        User user = createUser();
        userRepository.save(user);

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        Comment comment = createComment(user, "내용", item);
        commentRepository.save(comment);

        CommentReplyRequestDto request = CommentReplyRequestDto.builder()
                .reply("답변작성완료")
                .build();

        // when
        CommentResponse commentResponse = commentService.updateCommentReply(item.getId(), comment.getId(), request, user.getId());

        // then
        assertThat(commentResponse.getId()).isNotNull();
        assertThat(commentResponse)
                .extracting("id", "reply")
                .contains(comment.getId(), request.getReply());
    }

    @DisplayName("댓글에 대한 답글을 등록할 때, 아이템 등록자가 아닌 사람이 등록하면 예외가 발생한다.")
    @Test
    void updateCommentReplyWithNotEqualUser() {
        // given
        User user = createUser();
        User anotherUser = createUser();
        userRepository.saveAll(List.of(user, anotherUser));

        Item item = createItem(user, 10_000, "제목", "내용", SALE);
        itemRepository.save(item);

        Comment comment = createComment(user, "내용", item);
        commentRepository.save(comment);

        CommentReplyRequestDto request = CommentReplyRequestDto.builder()
                .reply("답변작성완료")
                .build();

        // when
        assertThatThrownBy(() -> commentService.updateCommentReply(item.getId(), comment.getId(), request, anotherUser.getId()))
                .isInstanceOf(MarketAppException.class)
                .hasMessage("작성자 정보가 일치하지 않습니다.");
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

    private Comment createComment(final User user, final String content, final Item item) {
        return Comment.builder()
                .user(user)
                .content(content)
                .item(item)
                .build();
    }
}