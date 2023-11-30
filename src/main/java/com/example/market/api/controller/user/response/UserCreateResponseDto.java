package com.example.market.api.controller.user.response;

import com.example.market.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserCreateResponseDto {
    private Long id;
    private String username;

    public UserCreateResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }
}
