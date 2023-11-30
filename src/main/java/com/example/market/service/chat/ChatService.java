package com.example.market.service.chat;

import com.example.market.domain.item.Item;
import com.example.market.domain.chat.Chat;
import com.example.market.domain.chat.ChatRoom;
import com.example.market.domain.user.User;
import com.example.market.api.controller.chat.request.ChatRoomCreateDto;
import com.example.market.api.controller.chat.request.ChatSenderDto;
import com.example.market.api.controller.chat.response.ChatRoomCreateResponseDto;
import com.example.market.api.controller.chat.response.ChatRoomListResponseDto;
import com.example.market.exception.MarketAppException;
import com.example.market.domain.item.ItemRepository;
import com.example.market.domain.chat.ChatRepository;
import com.example.market.domain.chat.ChatRoomRepository;
import com.example.market.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.market.exception.ErrorCode.NOT_FOUND_ITEM;
import static com.example.market.exception.ErrorCode.SERVER_ERROR;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ChatRepository chatRepository;

    public List<ChatRoomListResponseDto> getChatRooms(Long userId) {
        User user = userRepository.findById(userId).get();
//        List<ChatRoom> allBySellerIdAndBuyerId = chatRoomRepository.findAllBySellerIdAndBuyerId(userId);
        List<ChatRoom> allBySellerIdAndBuyerId = chatRoomRepository.findAllBySellerIdAndBuyerId(user);
        List<ChatRoomListResponseDto> collect = allBySellerIdAndBuyerId.stream()
                .map(r -> new ChatRoomListResponseDto(r))
                .collect(Collectors.toList());
        return collect;
    }

    @Transactional
    public ChatRoomCreateResponseDto createChatRoom(ChatRoomCreateDto createDto, Long userId) {
        Long itemId = createDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));

        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new MarketAppException(SERVER_ERROR, SERVER_ERROR.getMessage()));

        User seller = item.getUser();


        ChatRoom save = chatRoomRepository.save(createDto.toEntity(item, buyer, seller));
        chatRepository.save(Chat.builder()
                .chatRoom(save)
                .writer("환영합니다")
                .content("환영합니다")
                .build());
        return new ChatRoomCreateResponseDto(save);
    }

    public ChatSenderDto findRoomById(Long id) {
        User user = userRepository.findById(id).get();

        return new ChatSenderDto(user.getUsername());
    }
}
