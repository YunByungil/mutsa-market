package com.example.market.api.controller.chat.response;

import com.example.market.domain.chat.ChatRoom;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoomCreateResponseDto {

    private Long id;
    private Long itemId;
    private String itemName;
    private String seller;
    private String buyer;

    public ChatRoomCreateResponseDto(ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.itemId = chatRoom.getItem().getId();
        this.itemName = chatRoom.getItem().getTitle();
        this.seller = chatRoom.getSeller().getUsername();
        this.buyer = chatRoom.getBuyer().getUsername();
    }
}
