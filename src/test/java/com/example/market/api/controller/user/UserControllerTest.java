package com.example.market.api.controller.user;

import com.example.market.ControllerTestSupport;
import com.example.market.domain.user.Coordinate;
import com.example.market.domain.user.SearchScope;
import com.example.market.api.controller.user.request.UserCreateRequestDto;
import com.example.market.api.controller.user.request.UserUpdateCoordinateRequest;
import com.example.market.api.controller.user.request.UserUpdateSearchScopeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTestSupport {


    @DisplayName("회원가입 테스트 신규 회원을 등록한다.")
    @Test
    @WithMockUser
    void createUser() throws Exception {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username("아이디")
                .password("비밀번호")
                .coordinate(new Coordinate(37.1, 127.1))
                .address("주소")
                .build();

        // when // then
        mockMvc.perform(
                        post("/join").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("회원가입할 때 아이디는 꼭 입력해야 한다.")
    @Test
    @WithMockUser
    void createUserWithEmptyUsername() throws Exception {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
//                .username("아이디")
                .password("비밀번호")
                .coordinate(new Coordinate(37.1, 127.1))
                .address("주소")
                .build();

        // when // then
        mockMvc.perform(
                        post("/join").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("아이디는 필수로 입력해야 됩니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("회원가입할 때 비밀번호는 꼭 입력해야 한다.")
    @Test
    @WithMockUser
    void createUserWithEmptyPassword() throws Exception {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username("아이디")
//                .password("비밀번호")
                .coordinate(new Coordinate(37.1, 127.1))
                .address("주소")
                .build();

        // when // then
        mockMvc.perform(
                        post("/join").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("비밀번호는 필수로 입력해야 됩니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("회원가입할 때 좌표값을 꼭 입력해야 한다.")
    @Test
    @WithMockUser
    void createUserWithEmptyLat() throws Exception {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username("아이디")
                .password("비밀번호")
//                .coordinate(new Coordinate(37.1, 127.1))
                .address("주소")
                .build();

        // when // then
        mockMvc.perform(
                        post("/join").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("좌표는 필수로 입력해야 됩니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("회원가입할 때 주소값을 꼭 입력해야 한다.")
    @Test
    void createUserWithEmptyAddress() throws Exception {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .username("아이디")
                .password("비밀번호")
                .coordinate(new Coordinate(37.1, 127.1))
//                .address("주소")
                .build();

        // when // then
        mockMvc.perform(
                        post("/join").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("주소는 필수로 입력해야 됩니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("좌표를 수정한다.")
    @Test
    void updateCoordinate() throws Exception {
        // given
        final Coordinate coordinate = Coordinate.builder()
                .lat(37.1234)
                .lng(127.1234)
                .build();

        UserUpdateCoordinateRequest request = UserUpdateCoordinateRequest.builder()
                .coordinate(coordinate)
                .build();

        // when // then
        mockMvc.perform(
                        put("/coordinate").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("좌표를 수정할 때, 위도와 경도(Coordinate) 값은 꼭 입력해야 한다.")
    @Test
    void updateCoordinateWithEmptyLatOrLng() throws Exception {
        // given
        UserUpdateCoordinateRequest request = UserUpdateCoordinateRequest.builder()
                .build();

        // when // then
        mockMvc.perform(
                        put("/coordinate").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("좌표는 필수로 입력해야 합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("회원이 설정한 상품 검색 범위를 수정한다.")
    @Test
    void updateSearchScope() throws Exception {
        // given
        UserUpdateSearchScopeRequest request = UserUpdateSearchScopeRequest.builder()
                .searchScope(SearchScope.WIDE)
                .build();

        // when // then
        mockMvc.perform(
                        put("/search-scope").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("회원이 설정한 상품 검색 범위를 수정할 때, 검색 범위는 필수 값이다.")
    @Test
    void updateSearchScopeWithEmptySearchScope() throws Exception {
        // given
        UserUpdateSearchScopeRequest request = UserUpdateSearchScopeRequest.builder()
                .build();

        // when // then
        mockMvc.perform(
                        put("/search-scope").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("검색 범위는 필수로 입력해야 합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}