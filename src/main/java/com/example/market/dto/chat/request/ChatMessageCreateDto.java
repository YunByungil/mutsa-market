package com.example.market.dto.chat.request;

import com.example.market.domain.entity.chat.Chat;
import com.example.market.domain.entity.chat.ChatRoom;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ChatMessageCreateDto {

    private Long roomId;
    private String writer;
    private String content;

//    @Builder
//    public ChatMessageCreateDto(final Long roomId, final String writer, final String content) {
//        this.roomId = roomId;
//        this.writer = writer;
//        this.content = content;
//    }

    public Chat toEntity(ChatRoom chatRoom) {
        return Chat.builder()
                .writer(writer)
                .content(content)
                .chatRoom(chatRoom)
                .build();
    }

}
