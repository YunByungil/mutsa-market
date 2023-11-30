package com.example.market.viewController;

import com.example.market.domain.chat.ChatRoom;
import com.example.market.api.controller.chat.request.ChatMessageCreateDto;
import com.example.market.domain.chat.ChatRepository;
import com.example.market.domain.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class WebSocketMapping {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository roomRepository;

    @MessageMapping("/chat")
    public void sendChat(ChatMessageCreateDto createDto,
                         @Headers Map<String, Object> headers,
                         @Header("nativeHeaders") Map<String, String> nativeHeaders) {
        log.info(createDto.toString());
        log.info(headers.toString());
        log.info(nativeHeaders.toString());
        for (String s : nativeHeaders.keySet()) {
            log.info("header = {}", s);
        }
        for (Object value : nativeHeaders.values()) {
            System.out.println("value = " + value);
        }

        ChatRoom chatRoom = roomRepository.findById(createDto.getRoomId()).get();
        chatRepository.save(createDto.toEntity(chatRoom));
        simpMessagingTemplate.convertAndSend(
                String.format("/topic/%s", createDto.getRoomId()),
                createDto
        );
    }

//    @SubscribeMapping("/topic/{roomId}") // 입장할 때 누가 입장했는지(누가 연결되었는지)
//    public ChatMessage sendGreet(@DestinationVariable("roomId") Long roomId) {
//        log.info("new subscription to {}", roomId);
//
//        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.setRoomId(roomId);
//        chatMessage.setSender("admin");
//        chatMessage.setMessage("hello!");
//        String time = new SimpleDateFormat("HH:mm").format(new Date());
//        chatMessage.setTime(time);
//
//        return chatMessage;
//    }

}
