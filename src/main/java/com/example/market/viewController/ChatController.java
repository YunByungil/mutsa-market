package com.example.market.viewController;

import com.example.market.domain.chat.Chat;
import com.example.market.domain.chat.ChatRoom;
import com.example.market.domain.chat.ChatRepository;
import com.example.market.domain.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/chat")
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository roomRepository;

    @GetMapping
    public String index() {
        return "chat-lobby";
    }

    @GetMapping("/{roomId}/{itemId}")
    public String enterRoom(@PathVariable Long roomId,
                            Model model) {
        ChatRoom chatRoom = roomRepository.findById(roomId).get();
        List<Chat> chats = chatRepository.customFindAllById(chatRoom);
        for (Chat chat : chats) {
            System.out.println("chat.getContent() = " + chat.getContent());
        }
        model.addAttribute("messages", chats);
//        System.out.println("chat.getId() = " + chat.getId());

        return "chat-room";
    }

    @GetMapping("/list")
    public String chatList() {
        return "chat-room-list";
    }
}
