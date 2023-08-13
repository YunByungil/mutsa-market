package com.example.market.repository.chat;

import com.example.market.domain.entity.chat.Chat;
import com.example.market.domain.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("select c " +
            "from Chat c " +
            "where c.chatRoom =:room")
    List<Chat> customFindAllById(@Param("room") ChatRoom room);
}
