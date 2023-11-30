package com.example.market.api.controller.chat.response;

import com.example.market.domain.chat.ChatRoom;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoomListResponseDto {

    private Long id;
    private String seller;
    private String buyer;
    private Long itemId;
    private String itemName;

    public ChatRoomListResponseDto(final ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.seller = chatRoom.getSeller().getUsername();
        this.buyer = chatRoom.getBuyer().getUsername();
        this.itemId = chatRoom.getItem().getId();
        this.itemName = chatRoom.getItem().getTitle();
    }
}
