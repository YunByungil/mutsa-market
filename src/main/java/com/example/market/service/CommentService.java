package com.example.market.service;

import com.example.market.domain.entity.Comment;
import com.example.market.domain.entity.Item;
import com.example.market.dto.comment.request.CommentCreateRequestDto;
import com.example.market.dto.comment.request.CommentUpdateRequestDto;
import com.example.market.dto.comment.response.CommentListResponseDto;
import com.example.market.repository.CommentRepository;
import com.example.market.repository.ItemRepository;
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

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public void create(Long itemId, CommentCreateRequestDto dto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        commentRepository.save(dto.toEntity(itemId));
    }

    public Page<CommentListResponseDto> readCommentList(Long itemId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").ascending());

        Page<Comment> findCommentByAllItemId = commentRepository.findAllByItemId(itemId, pageable);

        Page<CommentListResponseDto> commentListResponseDto = findCommentByAllItemId.map(CommentListResponseDto::new);

        return commentListResponseDto;
    }

    @Transactional
    public void updateComment(Long itemId, Long commentId, CommentUpdateRequestDto dto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        validateItemIdMatch(item, comment);
        checkWriterAndPassword(dto, comment);

        comment.update(dto);
    }

    private void checkWriterAndPassword(CommentUpdateRequestDto dto, Comment comment) {
        if (!comment.getWriter().equals(dto.getWriter())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (!comment.getPassword().equals(dto.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void validateItemIdMatch(Item item, Comment comment) {
        if (item.getId() != comment.getItemId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

}
