package com.example.market.dto.comment.request;

import com.example.market.domain.entity.Comment;
import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentCreateRequestDto {

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;

    @Builder
    public CommentCreateRequestDto(String content) {
        this.content = content;
    }

    public Comment toEntity(Item item, User user) {
        return Comment.builder()
                .item(item)
                .user(user)
                .content(content)
                .build();
    }
}
