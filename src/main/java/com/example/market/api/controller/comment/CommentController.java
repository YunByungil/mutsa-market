package com.example.market.api.controller.comment;

import com.example.market.api.ApiResponse;
import com.example.market.dto.comment.request.CommentCreateRequestDto;
import com.example.market.dto.comment.request.CommentReplyRequestDto;
import com.example.market.dto.comment.request.CommentUpdateRequestDto;
import com.example.market.dto.comment.response.CommentListResponseDto;
import com.example.market.dto.comment.response.CommentResponse;
import com.example.market.dto.comment.response.CommentResponseDto;
import com.example.market.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.market.common.SystemMessage.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/items/{itemId}/comments")
    public ApiResponse<CommentResponse> create(@PathVariable Long itemId,
                                                 @Valid @RequestBody CommentCreateRequestDto dto,
                                                 Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        return ApiResponse.ok(commentService.create(itemId, dto, userId));
    }

    @GetMapping("/items/{itemId}/comments")
    public ApiResponse<Page<CommentResponse>> readCommentList(@PathVariable Long itemId,
                                                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                              @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
//        Page<CommentListResponseDto> commentListResponseDto = commentService.readCommentList(itemId, page, limit);

        return ApiResponse.ok(commentService.readCommentList(itemId, page, limit));
    }

    @PutMapping("/items/{itemId}/comments/{commentId}")
    public ApiResponse<CommentResponseDto> updateComment(@PathVariable Long itemId,
                                            @PathVariable Long commentId,
                                            @Valid @RequestBody CommentUpdateRequestDto dto,
                                            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        commentService.updateComment(itemId, commentId, dto, userId);

        return ApiResponse.ok(new CommentResponseDto(UPDATE_COMMENT));
    }

    @DeleteMapping("/items/{itemId}/comments/{commentId}")
    public ApiResponse<CommentResponseDto> deleteComment(@PathVariable Long itemId,
                                            @PathVariable Long commentId,
                                            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        commentService.deleteComment(itemId, commentId, userId);

        return ApiResponse.ok(new CommentResponseDto(DELETE_COMMENT));
    }


    @PutMapping("/items/{itemId}/comments/{commentId}/reply")
    public ApiResponse<CommentResponseDto> updateCommentReply(@PathVariable Long itemId,
                                                 @PathVariable Long commentId,
                                                 @Valid @RequestBody CommentReplyRequestDto replyDto,
                                                 Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        commentService.updateCommentReply(itemId, commentId, replyDto, userId);

        return ApiResponse.ok(new CommentResponseDto(REGISTER_REPLY));
    }
}
