package com.example.market.dto.chat.request;

import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.chat.ChatRoom;
import com.example.market.domain.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoomCreateDto {

    private Long itemId;
    private String roomName;

    @Builder
    public ChatRoomCreateDto(final Long itemId, final String roomName) {
        this.itemId = itemId;
        this.roomName = roomName;
    }

    public ChatRoom toEntity(Item item, User buyer, User seller) {
        return ChatRoom.builder()
                .item(item)
                .buyer(buyer)
                .seller(seller)
                .build();
    }
}
