package com.example.market.controller.user;

import com.example.market.ControllerTestSupport;
import com.example.market.domain.entity.Comment;
import com.example.market.domain.entity.user.Address;
import com.example.market.domain.entity.user.Coordinate;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
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
}