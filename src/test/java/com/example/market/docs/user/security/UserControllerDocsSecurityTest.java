package com.example.market.docs.user.security;

import com.example.market.domain.user.User;
import com.example.market.api.controller.user.request.UserLoginRequest;
import com.example.market.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class UserControllerDocsSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("로그인 API")
    @Test
    void login() throws Exception {
        User user = User.builder()
                .username("아이디")
                .password(passwordEncoder.encode("비밀번호"))
                .build();
        userRepository.save(user);

        UserLoginRequest login = UserLoginRequest.builder()
                .username("아이디")
                .password("비밀번호")
                .build();

        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(login))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("username").type(STRING)
                                        .description("아이디"),
                                fieldWithPath("password").type(STRING)
                                        .description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.accessToken").type(STRING)
                                        .description("액세스 토큰")
                        )
                        ));
    }

    @DisplayName("로그인 시 username이 일치하지 않으면 예외가 발생한다.")
    @Test
    void loginWithNoUser() throws Exception {
        User user = User.builder()
                .username("아이디")
                .password(passwordEncoder.encode("비밀번호"))
                .build();
        userRepository.save(user);

        UserLoginRequest login = UserLoginRequest.builder()
                .username("틀린 아이디")
                .password("비밀번호")
                .build();

        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(login))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("로그인 시 password가 일치하지 않으면 예외가 발생한다.")
    @Test
    void loginWithNoPassword() throws Exception {
        User user = User.builder()
                .username("아이디")
                .password(passwordEncoder.encode("비밀번호"))
                .build();
        userRepository.save(user);

        UserLoginRequest login = UserLoginRequest.builder()
                .username("아이디")
                .password("틀린 비밀번호")
                .build();

        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(login))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
