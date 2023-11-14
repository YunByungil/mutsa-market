package com.example.market.dto.user.response;

import com.example.market.domain.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserResponse {

    private Long id;
    private String username;
    private String password;

    @Builder
    public UserResponse(final Long id, final String username, final String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public static UserResponse of(final User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}
