package com.example.market.dto.comment.response;

import com.example.market.domain.entity.Comment;
import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentResponse {

    private Long id;
    private String content;
    private String reply;
    private String username;
    private Long itemId;

    @Builder
    public CommentResponse(final Long id, final String content, final String reply, final String username, final Long itemId) {
        this.id = id;
        this.content = content;
        this.reply = reply;
        this.username = username;
        this.itemId = itemId;
    }

    public static CommentResponse of(final Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .reply(comment.getReply())
                .username(comment.getUser().getUsername())
                .itemId(comment.getItem().getId())
                .build();
    }
}
