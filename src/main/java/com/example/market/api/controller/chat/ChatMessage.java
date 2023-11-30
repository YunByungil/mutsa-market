package com.example.market.api.controller.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

//@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private Long roomId;
    private String sender;
    private String message;
    private String time = "10:10";
}
