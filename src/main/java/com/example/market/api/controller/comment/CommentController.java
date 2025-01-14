package com.example.market.api.controller.comment;

import com.example.market.api.ApiResponse;
import com.example.market.api.controller.comment.request.CommentCreateRequestDto;
import com.example.market.api.controller.comment.request.CommentReplyRequestDto;
import com.example.market.api.controller.comment.request.CommentUpdateRequestDto;
import com.example.market.api.controller.comment.response.CommentResponse;
import com.example.market.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<CommentResponse> updateComment(@PathVariable Long itemId,
                                            @PathVariable Long commentId,
                                            @Valid @RequestBody CommentUpdateRequestDto dto,
                                            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        return ApiResponse.ok(commentService.updateComment(itemId, commentId, dto, userId));
    }

    @DeleteMapping("/items/{itemId}/comments/{commentId}")
    public ApiResponse<CommentResponse> deleteComment(@PathVariable Long itemId,
                                            @PathVariable Long commentId,
                                            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        return ApiResponse.ok(commentService.deleteComment(itemId, commentId, userId));
    }


    @PutMapping("/items/{itemId}/comments/{commentId}/reply")
    public ApiResponse<CommentResponse> updateCommentReply(@PathVariable Long itemId,
                                                 @PathVariable Long commentId,
                                                 @Valid @RequestBody CommentReplyRequestDto replyDto,
                                                 Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        return ApiResponse.ok(commentService.updateCommentReply(itemId, commentId, replyDto, userId));
    }
}
