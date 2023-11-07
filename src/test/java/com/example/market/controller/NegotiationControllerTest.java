package com.example.market.controller;

import com.example.market.ControllerTestSupport;
import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.Negotiation;
import com.example.market.domain.entity.enums.NegotiationStatus;
import com.example.market.domain.entity.enums.Role;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.negotiation.request.NegotiationCreateRequestDto;
import com.example.market.dto.negotiation.request.NegotiationUpdateRequestDto;
import com.example.market.repository.CommentRepository;
import com.example.market.repository.ItemRepository;
import com.example.market.repository.NegotiationRepository;
import com.example.market.repository.user.UserRepository;
import com.example.market.service.CommentService;
import com.example.market.service.NegotiationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NegotiationControllerTest extends ControllerTestSupport {

    @DisplayName("가격을 제안합니다.")
    @Test
    void createProposals() throws Exception {
        // given
        NegotiationCreateRequestDto request = NegotiationCreateRequestDto.builder()
                .status(NegotiationStatus.SUGGEST)
                .suggestedPrice(5_000)
                .build();

        // when // then
        mockMvc.perform(
                        post("/items/{itemId}/proposals", 1L).with(csrf())
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("가격을 제안할 때, 제안 상태는 필수입니다.")
    @Test
    void createNegotiationWithoutNegotiationStatus() throws Exception {
        // given
        NegotiationCreateRequestDto request = NegotiationCreateRequestDto.builder()
//                .status(NegotiationStatus.SUGGEST)
                .suggestedPrice(5_0000)
                .build();

        // when // then
        mockMvc.perform(
                        post("/items/{itemId}/proposals", 1L).with(csrf())
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("제안 상태는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("가격을 제안할 때, 가격은 양수여야 합니다.")
    @Test
    void createNegotiationWithZeroPrice() throws Exception {
        // given
        NegotiationCreateRequestDto request = NegotiationCreateRequestDto.builder()
                .status(NegotiationStatus.SUGGEST)
                .suggestedPrice(0)
                .build();

        // when // then
        mockMvc.perform(
                        post("/items/{itemId}/proposals", 1L).with(csrf())
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("가격은 양수여야 합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("가격을 제안 받은 내역을 불러온다. (내가 받은 제안들)")
    @Test
    void getReceivedNegotiationItems() throws Exception {
        // when // then
        mockMvc.perform(
                        get("/items/received/proposals").with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("가격을 제안 보낸 내역을 불러온다. (내가 보낸 제안들)")
    @Test
    void getSentNegotiationItems() throws Exception {
        // when // then
        mockMvc.perform(
                        get("/items/sent/proposals").with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }
}