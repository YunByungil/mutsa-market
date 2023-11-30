package com.example.market.domain.chat;

import com.example.market.domain.chat.ChatRoom;
import com.example.market.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("select c " +
            "from ChatRoom c " +
            "where c.buyer =:user or c.seller =:user")
    List<ChatRoom> findAllBySellerIdAndBuyerId(@Param("user") User user);
//    @Query("select c " +
//            "from ChatRoom c " +
//            "where c.buyer.id =:userId or c.seller.id =:userId")
//    List<ChatRoom> findAllBySellerIdAndBuyerId(@Param("userId") Long userId);
}
