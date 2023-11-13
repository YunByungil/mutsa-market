package com.example.market.docs.user;

import com.example.market.api.controller.user.UserController;
import com.example.market.docs.RestDocsSupport;
import com.example.market.domain.entity.user.Address;
import com.example.market.dto.user.request.UserCreateRequestDto;
import com.example.market.dto.user.response.UserCreateResponseDto;
import com.example.market.dto.user.response.UserResponse;
import com.example.market.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerDocsTest extends RestDocsSupport {

    private final UserService userService = mock(UserService.class);

    @Override
    protected Object initController() {
        return new UserController(userService);
    }

    @DisplayName("회원가입 API")
    @Test
    void createUser() throws Exception {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username("아이디")
                .password("비밀번호")
                .nickname("닉네임")
                .email("email@email.com")
                .userImage("profile.jpg")
                .phoneNumber("010-1234-5678")
                .address(new Address("고양시", "일산서구", "12345"))
                .build();

        given(userService.createUser(any(UserCreateRequestDto.class)))
                .willReturn(UserResponse.builder()
                        .id(1L)
                        .username("아이디")
                        .password("비밀번호")
                        .build());

        mockMvc.perform(
                        post("/join")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("username").type(STRING)
                                        .description("아이디"),
                                fieldWithPath("password").type(STRING)
                                        .description("비밀번호"),
                                fieldWithPath("phoneNumber").type(STRING)
                                        .optional()
                                        .description("연락처"),
                                fieldWithPath("email").type(STRING)
                                        .optional()
                                        .description("이메일"),
                                fieldWithPath("nickname").type(STRING)
                                        .optional()
                                        .description("닉네임"),
                                fieldWithPath("address.city").type(STRING)
                                        .optional()
                                        .description("시/군/구"),
                                fieldWithPath("address.street").type(STRING)
                                        .optional()
                                        .description("동/읍/면/리"),
                                fieldWithPath("address.zipcode").type(STRING)
                                        .optional()
                                        .description("우편번호"),
                                fieldWithPath("userImage").type(STRING)
                                        .optional()
                                        .description("이미지")
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
                                fieldWithPath("data.id").type(NUMBER)
                                        .description("회원 ID"),
                                fieldWithPath("data.username").type(STRING)
                                        .description("아이디"),
                                fieldWithPath("data.password").type(STRING)
                                        .description("비밀번호")
                        )
                ));
    }
}
