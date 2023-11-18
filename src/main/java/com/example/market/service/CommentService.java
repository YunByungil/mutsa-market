package com.example.market.service;

import com.example.market.domain.entity.Comment;
import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.comment.request.CommentCreateRequestDto;
import com.example.market.dto.comment.request.CommentReplyRequestDto;
import com.example.market.dto.comment.request.CommentUpdateRequestDto;
import com.example.market.dto.comment.response.CommentListResponseDto;
import com.example.market.dto.comment.response.CommentResponse;
import com.example.market.exception.ErrorCode;
import com.example.market.exception.MarketAppException;
import com.example.market.repository.CommentRepository;
import com.example.market.repository.ItemRepository;
import com.example.market.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.example.market.exception.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse create(Long itemId, CommentCreateRequestDto dto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Comment comment = commentRepository.save(dto.toEntity(item, user));

        return CommentResponse.of(comment);
    }

    public Page<CommentResponse> readCommentList(Long itemId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").ascending());

        Page<Comment> findCommentByAllItemId = commentRepository.findAllByItemId(itemId, pageable);

        Page<CommentResponse> commentListResponseDto = findCommentByAllItemId.map(CommentResponse::of);

        return commentListResponseDto;
    }

    @Transactional
    public CommentResponse updateComment(Long itemId, Long commentId, CommentUpdateRequestDto dto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_COMMENT, NOT_FOUND_COMMENT.getMessage()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (comment.getUser().getId() != user.getId()) {
            throw new MarketAppException(INVALID_WRITER, INVALID_WRITER.getMessage());
        }

        comment.update(dto);
        return CommentResponse.of(comment);
    }

    @Transactional
    public CommentResponse deleteComment(Long itemId, Long commentId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_COMMENT, NOT_FOUND_COMMENT.getMessage()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (comment.getUser().getId() != user.getId()) {
            throw new MarketAppException(INVALID_WRITER, INVALID_WRITER.getMessage());
        }

        commentRepository.delete(comment);
        return CommentResponse.of(comment);
    }

    @Transactional
    public CommentResponse updateCommentReply(Long itemId, Long commentId, CommentReplyRequestDto replyDto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_COMMENT, NOT_FOUND_COMMENT.getMessage()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (item.getUser().getId() != user.getId()) {
            throw new MarketAppException(INVALID_WRITER, INVALID_WRITER.getMessage());
        }

        comment.updateCommentReply(replyDto);
        return CommentResponse.of(comment);
    }

}
