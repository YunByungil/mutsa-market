package com.example.market.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentReplyRequestDto {

    @NotBlank
    private String reply;

    @Builder
    public CommentReplyRequestDto(String reply) {
        this.reply = reply;
    }
}
