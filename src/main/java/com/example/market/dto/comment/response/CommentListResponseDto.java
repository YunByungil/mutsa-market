package com.example.market.dto.comment.response;

import com.example.market.domain.entity.Comment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentListResponseDto {

    private Long id;
    private String content;
    private String reply;

    public CommentListResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.reply = comment.getReply();
    }
}
