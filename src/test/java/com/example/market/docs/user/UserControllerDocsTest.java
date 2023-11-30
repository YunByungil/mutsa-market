package com.example.market.docs.user;

import com.example.market.api.controller.user.UserController;
import com.example.market.docs.RestDocsSupport;
import com.example.market.domain.entity.user.Address;
import com.example.market.domain.entity.user.Coordinate;
import com.example.market.domain.entity.user.SearchScope;
import com.example.market.dto.user.request.UserCreateRequestDto;
import com.example.market.dto.user.request.UserUpdateCoordinateRequest;
import com.example.market.dto.user.request.UserUpdateSearchScopeRequest;
import com.example.market.dto.user.response.UserCreateResponseDto;
import com.example.market.dto.user.response.UserResponse;
import com.example.market.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.Authentication;

import static com.example.market.domain.entity.user.SearchScope.WIDE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
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
                .address("주소")
                .coordinate(new Coordinate(37.1, 127.1))
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
                                fieldWithPath("address").type(STRING)
                                        .description("주소"),
                                fieldWithPath("userImage").type(STRING)
                                        .optional()
                                        .description("이미지"),
                                fieldWithPath("coordinate.lat").type(NUMBER)
                                        .description("위도값"),
                                fieldWithPath("coordinate.lng").type(NUMBER)
                                        .description("경도값")
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

    @DisplayName("좌표 변경 API")
    @Test
    void updateCoordinate() throws Exception {
        // given
        Authentication authentication = getAuthentication();

        UserUpdateCoordinateRequest request = UserUpdateCoordinateRequest.builder()
                .coordinate(new Coordinate(37.1234, 127.1234))
                .build();

        given(userService.updateCoordinate(anyLong(), any(UserUpdateCoordinateRequest.class)))
                .willReturn(UserResponse.builder()
                        .id(1L)
                        .username("아이디")
                        .password("비밀번호")
                        .build());

        mockMvc.perform(
                        put("/coordinate")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-update-coordinate",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("coordinate.lat").type(NUMBER)
                                        .description("위도값"),
                                fieldWithPath("coordinate.lng").type(NUMBER)
                                        .description("경도값")
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

    @DisplayName("상품 검색 범위 수정 API")
    @Test
    void updateSearchScope() throws Exception {
        // given
        Authentication authentication = getAuthentication();

        UserUpdateSearchScopeRequest request = UserUpdateSearchScopeRequest.builder()
                .searchScope(WIDE)
                .build();

        given(userService.updateSearchScope(anyLong(), any(UserUpdateSearchScopeRequest.class)))
                .willReturn(UserResponse.builder()
                        .id(1L)
                        .username("아이디")
                        .password("비밀번호")
                        .build());

        mockMvc.perform(
                        put("/search-scope")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-update-searchScope",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("searchScope").type(STRING)
                                        .description("검색 범위")
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

    private Authentication getAuthentication() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("1");
        return authentication;
    }
}
