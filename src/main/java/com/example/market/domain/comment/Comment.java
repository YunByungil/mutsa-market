package com.example.market.domain.comment;

import com.example.market.domain.item.Item;
import com.example.market.domain.user.User;
import com.example.market.api.controller.comment.request.CommentReplyRequestDto;
import com.example.market.api.controller.comment.request.CommentUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private String reply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Builder
    public Comment(Item item, User user, String content, String reply) {
        this.item = item;
        this.content = content;
        this.reply = reply;
        this.user = user;
    }

    public void update(CommentUpdateRequestDto dto) {
        this.content = dto.getContent();
    }

    public void updateCommentReply(CommentReplyRequestDto dto) {
        this.reply = dto.getReply();
    }
}
