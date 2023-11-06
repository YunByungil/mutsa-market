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
    private User user;
    private Item item;

    @Builder
    public CommentResponse(final Long id, final String content, final String reply, final User user, final Item item) {
        this.id = id;
        this.content = content;
        this.reply = reply;
        this.user = user;
        this.item = item;
    }

    public static CommentResponse of(final Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .reply(comment.getReply())
                .user(comment.getUser())
                .item(comment.getItem())
                .build();
    }
}
