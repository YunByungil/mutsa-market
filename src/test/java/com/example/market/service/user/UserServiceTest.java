package com.example.market.service.user;

import com.example.market.domain.entity.user.User;
import com.example.market.dto.user.request.UserCreateRequestDto;
import com.example.market.exception.MarketAppException;
import com.example.market.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void end() {
        userRepository.deleteAll();
    }

    @DisplayName("회원가입 완료 테스트")
    @Test
    void createUser() {
        // given
        final String username = "아이디";
        final String password = "비밀번호";
        UserCreateRequestDto createDto = UserCreateRequestDto.builder()
                .username(username)
                .password(password)
                .build();

        userService.createUser(createDto);

        // when
        User user = userRepository.findByUsername(username).get();

        // then
        assertThat(user.getUsername()).isEqualTo(username);
    }

    @DisplayName("회원가입 중복 검사 테스트")
    @Test
    void duplicateUsername() {
        // given
        final String username = "아이디";
        final String password = "비밀번호";
        UserCreateRequestDto createDto = UserCreateRequestDto.builder()
                .username(username)
                .password(password)
                .build();


        // when
        userService.createUser(createDto);

        // then
        assertThatThrownBy(() -> {
            userService.createUser(createDto);
        }).isInstanceOf(MarketAppException.class);
    }
}