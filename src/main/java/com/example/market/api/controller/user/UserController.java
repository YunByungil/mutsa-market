package com.example.market.api.controller.user;

import com.example.market.api.ApiResponse;
import com.example.market.dto.user.request.UserCreateRequestDto;
import com.example.market.dto.user.response.UserCreateResponseDto;
import com.example.market.dto.user.response.UserResponse;
import com.example.market.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreateRequestDto createDto) {
        return ApiResponse.ok(userService.createUser(createDto));
    }
}
