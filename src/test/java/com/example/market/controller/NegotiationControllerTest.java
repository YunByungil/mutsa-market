package com.example.market.controller;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class})
@SpringBootTest
class NegotiationControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;


    @Autowired
    NegotiationRepository negotiationRepository;

    @Autowired
    NegotiationService negotiationService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    User user;
    User buyer;
    User buyer2;
    Item item;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        negotiationRepository.deleteAll();

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withResponseDefaults(prettyPrint())
                        .withRequestDefaults(prettyPrint()))
                .build();

        user = userRepository.save(User.builder()
                .username("판매자")
                .password("비밀번호")
                .role(Role.ADMIN)
                .build());
        buyer = userRepository.save(User.builder()
                .username("구매자")
                .password("비밀번호")
                .role(Role.USER)
                .build());
        buyer2 = userRepository.save(User.builder()
                .username("구매자2")
                .password("비밀번호")
                .role(Role.USER)
                .build());

        item = itemRepository.save(Item.builder()
                .title("제목")
                .description("설명")
                .minPriceWanted(10_000)
                .imageUrl("사진")
                .user(user)
                .build());

        SecurityContext context1 = SecurityContextHolder.getContext();
        context1.setAuthentication(new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword(), new ArrayList<>()));
    }

//    @AfterEach
//    void end() {
//        itemRepository.deleteAll();
//        negotiationRepository.deleteAll();
//    }

    @DisplayName("구매 제안 등록 API")
    @Test
    void createNegotiation() throws Exception {
        // given
        final int price = 5_000;
        NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
                .suggestedPrice(price)
                .build();

        String url = "http://localhost:8080/items/{itemId}/proposals";

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("" + buyer.getId());

        // when
        mvc.perform(post(url, item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication)
                        .content(new ObjectMapper().writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andDo(document("/proposals",
                        requestFields(
                                fieldWithPath("suggestedPrice").description("제안 가격")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("물품 ID")
                        )));
        // then

    }

    @DisplayName("구매 제안 가격 수정 API")
    @Test
    void updateNegotiation() throws Exception {
        // given
        final int price = 5_000;
        final int updatePrice = 10_000;
        Long negotiationId = createNegotiationOne(price, buyer);

        NegotiationUpdateRequestDto updateDto = NegotiationUpdateRequestDto.builder()
                .suggestedPrice(updatePrice)
                .build();

        String url = "http://localhost:8080/items/{itemId}/proposals/{proposalId}";

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("" + buyer.getId());

        // when
        mvc.perform(put(url, item.getId(), negotiationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication)
                        .content(new ObjectMapper().writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andDo(document("/proposals-update",
                        requestFields(
                                fieldWithPath("suggestedPrice").description("수정한 제안 가격"),
                                fieldWithPath("status").description("null")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("물품 ID"),
                                parameterWithName("proposalId").description("제안 ID")

                        )));

        // then

    }

    @DisplayName("구매 제안 삭제 API")
    @Test
    void deleteNegotiation() throws Exception {
        // given
        final int price = 5_000;
        Long negotiationId = createNegotiationOne(price, buyer);

        String url = "http://localhost:8080/items/{itemId}/proposals/{proposalId}";

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("" + buyer.getId());

        // when
        mvc.perform(delete(url, item.getId(), negotiationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andDo(document("/proposals-delete",
                        pathParameters(
                                parameterWithName("itemId").description("물품 ID"),
                                parameterWithName("proposalId").description("제안 ID")

                        )));

        // then

    }

    @DisplayName("구매 제안 상태 변경 API")
    @Test
    void updateNegotiationStatus() throws Exception {
        // given
        final int price = 5_000;
        Long negotiationId = createNegotiationOne(price, buyer);

        NegotiationUpdateRequestDto updateDto = NegotiationUpdateRequestDto.builder()
                .status(NegotiationStatus.ACCEPT.getStatus())
                .build();

        String url = "http://localhost:8080/items/{itemId}/proposals/{proposalId}";

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("" + user.getId());

        // when
        mvc.perform(put(url, item.getId(), negotiationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication)
                        .content(new ObjectMapper().writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andDo(document("/proposals-statusUpdate",
                        requestFields(
                                fieldWithPath("status").description("제안 상태 - 수락 or 거절"),
                                fieldWithPath("suggestedPrice").description("0")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("물품 ID"),
                                parameterWithName("proposalId").description("제안 ID")

                        )));

        // then

    }

    @DisplayName("구매 확정 API")
    @Test
    void purchaseConfirm() throws Exception {
        // given
        final int price = 5_000;
        Long negotiationId = createNegotiationStatusIsAccept(price, buyer);


        NegotiationUpdateRequestDto updateDto = NegotiationUpdateRequestDto.builder()
                .status(NegotiationStatus.CONFIRM.getStatus())
                .build();

        String url = "http://localhost:8080/items/{itemId}/proposals/{proposalId}";

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("" + buyer.getId());

        // when
        mvc.perform(put(url, item.getId(), negotiationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication)
                        .content(new ObjectMapper().writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andDo(document("/proposals-purchaseConfirm",
                        requestFields(
                                fieldWithPath("status").description(NegotiationStatus.CONFIRM.getStatus()),
                                fieldWithPath("suggestedPrice").description("0")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("물품 ID"),
                                parameterWithName("proposalId").description("제안 ID")

                        )));

        // then

    }

    // TODO
//    @DisplayName("제안 작성자 기준 구매 제안 페이징 조회 API")
//    @Test
//    void readAllNegotiationByBuyer() throws Exception {
//        // given
//        final int price = 5_000;
//        for (int i = 1; i <= 5; i++) {
//            NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
//                    .suggestedPrice(price * i)
//                    .build();
//
//            negotiationService.createNegotiation(item.getId(), createDto, buyer.getId());
//        }
//
//        String url = "http://localhost:8080/items/{itemId}/proposals";
//
//
//        // when
//        mvc.perform(get(url, itemId)
//                        .param("page", "0")
//                        .param("limit", "5")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(listDto)))
//                .andExpect(status().isOk())
//                .andDo(document("/proposals-get-all-buyer",
//                        pathParameters(
//                                parameterWithName("itemId").description("아이템 ID")
//                        ),
//                        queryParameters(
//                                parameterWithName("page").description("현재 페이지"),
//                                parameterWithName("limit").description("한 페이지당 조회할 데이터 개수")
//                        ),
//                        requestFields(
//                                fieldWithPath("writer").description("제안 작성자"),
//                                fieldWithPath("password").description("제안 비밀번호")
//                        )));
//        // then
//    }

    @DisplayName("판매자 기준 구매 제안 페이징 조회 API")
    @Test
    void readAllNegotiationBySeller() throws Exception {
        // given
        final int price = 5_000;
        for (int i = 1; i <= 5; i++) {
            NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
                    .suggestedPrice(price * i)
                    .build();

            negotiationService.createNegotiation(item.getId(), createDto, buyer.getId());
        }

        String url = "http://localhost:8080/items/{itemId}/proposals";

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("" + user.getId());

        // when
        mvc.perform(get(url, item.getId())
                        .param("page", "0")
                        .param("limit", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andDo(document("/proposals-get-all-seller",
                        pathParameters(
                                parameterWithName("itemId").description("아이템 ID")
                        ),
                        queryParameters(
                                parameterWithName("page").description("현재 페이지"),
                                parameterWithName("limit").description("한 페이지당 조회할 데이터 개수")
                        )));

        // then

    }

    private Long createNegotiationOne(int price, User buyer) {
        NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
                .suggestedPrice(price)
                .build();

        Negotiation negotiation = negotiationRepository.save(createDto.toEntity(item, buyer));

        return negotiation.getId();
    }

    private Long createNegotiationStatusIsAccept(int price, User buyer) {
        NegotiationCreateRequestDto createDto = NegotiationCreateRequestDto.builder()
                .suggestedPrice(price)
                .build();

        Negotiation negotiation = negotiationRepository.save(createDto.toEntity(item, buyer));

        NegotiationUpdateRequestDto updateStatusDto = NegotiationUpdateRequestDto.builder()
                .status("수락")
                .build();

        negotiationService.updateNegotiation(item.getId(), negotiation.getId(), updateStatusDto, user.getId());

        return negotiation.getId();
    }
}