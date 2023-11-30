package com.example.market.api.controller.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserLoginRequest {

    @NotBlank(message = "아이디는 필수로 입력해야 됩니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수로 입력해야 됩니다.")
    private String password;

    @Builder
    public UserLoginRequest(final String username, final String password) {
        this.username = username;
        this.password = password;
    }
}
