package com.example.market.service.user;

import com.example.market.IntegrationTestSupport;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.user.request.UserCreateRequestDto;
import com.example.market.dto.user.request.UserLoginRequest;
import com.example.market.dto.user.response.UserResponse;
import com.example.market.exception.MarketAppException;
import com.example.market.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @AfterEach
    void end() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("아이디와 비밀번호를 받아 새로운 회원을 생성한다.")
    @Test
    void createUser() {
        // given
        final String username = "아이디";
        final String password = "비밀번호";
        UserCreateRequestDto request = createUserRequest(username, password);

        // when
        UserResponse userResponse = userService.createUser(request);
        final String encodedPassword = userResponse.getPassword();

        // then
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse)
                .extracting("username", "password")
                .contains(
                        "아이디", encodedPassword
                );
    }

    @DisplayName("이미 가입되어있는 아이디로 회원을 생성하려는 경우 예외가 발생한다.")
    @Test
    void createUserWithDuplicateUsername() {
        // given
        final String username = "아이디";
        final String password = "비밀번호";
        UserCreateRequestDto createDto = createUserRequest(username, password);
        userRepository.save(createDto.toEntity(password));


        // when // then
        assertThatThrownBy(() -> {
            userService.createUser(createDto);
        }).isInstanceOf(MarketAppException.class);
    }

    private UserCreateRequestDto createUserRequest(final String username, final String password) {
        return UserCreateRequestDto.builder()
                .username(username)
                .password(password)
                .build();
    }
}