package com.example.market.controller.user;

import com.example.market.domain.entity.Comment;
import com.example.market.domain.entity.user.Address;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.user.request.UserCreateRequestDto;
import com.example.market.repository.CommentRepository;
import com.example.market.repository.ItemRepository;
import com.example.market.repository.user.UserRepository;
import com.example.market.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class})
@SpringBootTest
@Transactional
class UserControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemRepository itemRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        userRepository.deleteAll();

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @DisplayName("회원가입 API 테스트")
    @Test
    void createUser() throws Exception {
        // given
        UserCreateRequestDto createDto = UserCreateRequestDto.builder()
                .username("아이디")
                .password("비밀번호")
                .email("이메일")
                .userImage("사진")
                .nickname("닉네임")
                .phoneNumber("번호")
                .address(new Address("city", "street", "zipcode"))
                .build();
        String url = "http://localhost:8080/join";

        // when
        ResultActions perform = mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)));

        // then
        perform.andExpect(status().isOk())
                .andDo(document("/join",
                        requestFields(
                                fieldWithPath("username").description("계정"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("userImage").description("사진"),
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("phoneNumber").description("번호"),
                                fieldWithPath("address.city").description("city"),
                                fieldWithPath("address.street").description("street"),
                                fieldWithPath("address.zipcode").description("zipcode")
                        )));

    }
}