package com.example.market.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Long itemId;
    private String writer;
    private String password;
    private String content;
    private String reply;

    @Builder
    public Comment(Long itemId, String writer, String password, String content, String reply) {
        this.itemId = itemId;
        this.writer = writer;
        this.password = password;
        this.content = content;
        this.reply = reply;
    }
}
