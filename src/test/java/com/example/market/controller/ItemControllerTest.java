package com.example.market.controller;

import com.example.market.ControllerTestSupport;
import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.enums.Role;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.item.request.ItemCreateRequestDto;
import com.example.market.dto.item.request.ItemUpdateRequestDto;
import com.example.market.dto.item.response.ItemResponse;
import com.example.market.repository.ItemRepository;
import com.example.market.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
import java.util.Collections;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemControllerTest extends ControllerTestSupport {

    @DisplayName("판매 상품을 등록한다.")
    @Test
    void createItem() throws Exception {
        // given
        ItemCreateRequestDto request = ItemCreateRequestDto.builder()
                .title("제목")
                .description("설명")
                .minPriceWanted(1_000)
                .build();

        // when // then
        mockMvc.perform(
                        post("/items").with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("판매 상품을 등록할 때, 제목은 필수값이다.")
    @Test
    void createItemWithoutTitle() throws Exception {
        // given
        ItemCreateRequestDto request = ItemCreateRequestDto.builder()
//                .title("제목")
                .description("설명")
                .minPriceWanted(1_000)
                .build();

        // when // then
        mockMvc.perform(
                        post("/items").with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("제목을 입력해주세요."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("판매 상품을 등록할 때, 내용 필수값이다.")
    @Test
    void createItemWithoutDescription() throws Exception {
        // given
        ItemCreateRequestDto request = ItemCreateRequestDto.builder()
                .title("제목")
//                .description("설명")
                .minPriceWanted(1_000)
                .build();

        // when // then
        mockMvc.perform(
                        post("/items").with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("내용을 입력해주세요."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("판매 상품을 등록할 때, 가격은 양수여야한다.")
    @Test
    void createItemWithZeroPrice() throws Exception {
        // given
        ItemCreateRequestDto request = ItemCreateRequestDto.builder()
                .title("제목")
                .description("설명")
                .minPriceWanted(0)
                .build();

        // when // then
        mockMvc.perform(
                        post("/items").with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("가격은 양수여야 합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("판매 상품을 단일 조회한다.")
    @Test
    void readItemOne() throws Exception {
        // given
        ItemResponse result = ItemResponse.builder().build();

        when(itemService.readItemOne(1L)).thenReturn(result);

        // when // then
        mockMvc.perform(
                        get("/items/{itemId}", 1)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @DisplayName("판매 상품을 전체 조회한다.")
    @Test
    void getSellingItems() throws Exception {
        // given
        Page<ItemResponse> result = new PageImpl<>(emptyList());

        when(itemService.readItemList(anyInt(), anyInt())).thenReturn(result);

        // when // then
        mockMvc.perform(
                        get("/items")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isMap());
    }

    @DisplayName("판매 상품을 수정한다.")
    @Test
    void updateItem() throws Exception {
        // given
        ItemUpdateRequestDto request = ItemUpdateRequestDto.builder()
                .title("제목 수정")
                .description("내용 수정")
                .minPriceWanted(3_000)
                .build();

        // when // then
        mockMvc.perform(
                        put("/items/{itemId}", 1L).with(csrf())
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

    @DisplayName("판매 상품을 수정할 때, 제목은 필수값이다.")
    @Test
    void updateItemWithoutTitle() throws Exception {
        // given
        ItemUpdateRequestDto request = ItemUpdateRequestDto.builder()
//                .title("제목 수정")
                .description("내용 수정")
                .minPriceWanted(3_000)
                .build();

        // when // then
        mockMvc.perform(
                        put("/items/{itemId}", 1L).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("제목을 입력해주세요."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("판매 상품을 수정할 때, 내용은 필수값이다.")
    @Test
    void updateItemWithoutDescription() throws Exception {
        // given
        ItemUpdateRequestDto request = ItemUpdateRequestDto.builder()
                .title("제목 수정")
//                .description("내용 수정")
                .minPriceWanted(3_000)
                .build();

        // when // then
        mockMvc.perform(
                        put("/items/{itemId}", 1L).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("내용을 입력해주세요."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("판매 상품을 수정할 때, 가격은 양수여야한다.")
    @Test
    void updateItemWithZeroPrice() throws Exception {
        // given
        ItemUpdateRequestDto request = ItemUpdateRequestDto.builder()
                .title("제목 수정")
                .description("내용 수정")
                .minPriceWanted(0)
                .build();

        // when // then
        mockMvc.perform(
                        put("/items/{itemId}", 1L).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("가격은 양수여야 합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("판매 상품을 삭제한다.")
    @Test
    void deleteItem() throws Exception {
        // when // then
        mockMvc.perform(
                delete("/items/{itemId}", 1).with(csrf())
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("판매 상품 이미지를 추가한다.")
    @Test
    void updateItemImage() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[]{});

        // when // then
        mockMvc.perform(
                multipart("/items/{itemId}/image", 1)
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())

        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

}