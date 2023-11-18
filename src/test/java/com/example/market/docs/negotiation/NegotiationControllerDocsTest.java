package com.example.market.docs.negotiation;

import com.example.market.api.controller.negotiation.NegotiationController;
import com.example.market.docs.RestDocsSupport;
import com.example.market.dto.negotiation.request.NegotiationCreateRequestDto;
import com.example.market.dto.negotiation.response.NegotiationResponse;
import com.example.market.service.NegotiationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

import java.util.List;

import static com.example.market.domain.entity.enums.NegotiationStatus.SUGGEST;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NegotiationControllerDocsTest extends RestDocsSupport {

    private final NegotiationService negotiationService = mock(NegotiationService.class);

    @Override
    protected Object initController() {
        return new NegotiationController(negotiationService);
    }

    @DisplayName("제안 등록 API")
    @Test
    void createNegotiation() throws Exception {
        Authentication authentication = getAuthentication();

        NegotiationCreateRequestDto request = NegotiationCreateRequestDto.builder()
                .status(SUGGEST)
                .suggestedPrice(5_000)
                .build();

        given(negotiationService.createNegotiation(anyLong(), any(NegotiationCreateRequestDto.class), anyLong()))
                .willReturn(NegotiationResponse.builder()
                        .id(1L)
                        .status(request.getStatus())
                        .suggestedPrice(request.getSuggestedPrice())
                        .itemId(1L)
                        .username("구매 희망자")
                        .build());

        mockMvc.perform(
                        post("/items/{itemId}/proposals", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("negotiation-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("status").type(STRING)
                                        .description("제안 상태"),
                                fieldWithPath("suggestedPrice").type(NUMBER)
                                        .description("제안 가격")
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
                                        .description("제안 ID"),
                                fieldWithPath("data.suggestedPrice").type(NUMBER)
                                        .description("제안 가격"),
                                fieldWithPath("data.status").type(STRING)
                                        .description("제안 상태"),
                                fieldWithPath("data.username").type(STRING)
                                        .description("제안자"),
                                fieldWithPath("data.itemId").type(NUMBER)
                                        .description("상품 ID")
                        )
                ));
    }
    
    @DisplayName("보낸 제안 목록 조회 API")
    @Test
    void getReceivedNegotiationItems() throws Exception {
        Authentication authentication = getAuthentication();

        List<NegotiationResponse> negotiationResponses = List.of(
                createNegotiationResponse(1L, "구매 희망자1", 8_000, 1L),
                createNegotiationResponse(2L, "구매 희망자2", 7_000, 1L),
                createNegotiationResponse(3L, "구매 희망자3", 6_000, 1L)
        );

        Page<NegotiationResponse> result = new PageImpl<>(negotiationResponses);
        given(negotiationService.getReceivedNegotiationItems(anyLong(), anyInt()))
                .willReturn(result);

        mockMvc.perform(
                        get("/items/received/proposals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                                .param("page", "0")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("negotiation-received-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page")
                                        .description("페이지")
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
                                fieldWithPath("data.content[].id").type(NUMBER)
                                        .description("제안 ID"),
                                fieldWithPath("data.content[].status").type(STRING)
                                        .description("제안 상태"),
                                fieldWithPath("data.content[].suggestedPrice").type(NUMBER)
                                        .description("제안 가격"),
                                fieldWithPath("data.content[].itemId").type(NUMBER)
                                        .description("아이템 ID"),
                                fieldWithPath("data.content[].username").type(STRING)
                                        .description("구매 희망자"),

                                fieldWithPath("data.last").
                                        description("마지막 페이지인지 여부"),
                                fieldWithPath("data.totalPages").
                                        description("전체 페이지 개수"),
                                fieldWithPath("data.totalElements").
                                        description("테이블 총 데이터 개수"),
                                fieldWithPath("data.first").
                                        description("첫번째 페이지인지 여부"),
                                fieldWithPath("data.numberOfElements").
                                        description("요청 페이지에서 조회 된 데이터 개수"),
                                fieldWithPath("data.number").
                                        description("현재 페이지 번호"),
                                fieldWithPath("data.size").
                                        description("한 페이지당 조회할 데이터 개수"),

                                fieldWithPath("data.sort.sorted").
                                        description("정렬 됐는지 여부"),
                                fieldWithPath("data.sort.unsorted").
                                        description("정렬 안 됐는지 여부"),
                                fieldWithPath("data.sort.empty").
                                        description("데이터가 비었는지 여부"),

                                fieldWithPath("data.empty").
                                        description("데이터가 비었는지 여부"),

                                fieldWithPath("data.pageable").
                                        description("페이징 정보")
                        )
                ));
    }
    
    @DisplayName("보낸 제안 목록 조회 API")
    @Test
    void getSentNegotiationItems() throws Exception {
        Authentication authentication = getAuthentication();

        List<NegotiationResponse> negotiationResponses = List.of(
                createNegotiationResponse(1L, "구매 희망자", 8_000, 1L),
                createNegotiationResponse(2L, "구매 희망자", 7_000, 2L),
                createNegotiationResponse(3L, "구매 희망자", 6_000, 3L)
        );

        Page<NegotiationResponse> result = new PageImpl<>(negotiationResponses);
        given(negotiationService.getSentNegotiationItems(anyLong(), anyInt()))
                .willReturn(result);

        mockMvc.perform(
                        get("/items/sent/proposals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                                .param("page", "0")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("negotiation-sent-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page")
                                        .description("페이지")
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
                                fieldWithPath("data.content[].id").type(NUMBER)
                                        .description("제안 ID"),
                                fieldWithPath("data.content[].status").type(STRING)
                                        .description("제안 상태"),
                                fieldWithPath("data.content[].suggestedPrice").type(NUMBER)
                                        .description("제안 가격"),
                                fieldWithPath("data.content[].itemId").type(NUMBER)
                                        .description("아이템 ID"),
                                fieldWithPath("data.content[].username").type(STRING)
                                        .description("구매 희망자"),

                                fieldWithPath("data.last").
                                        description("마지막 페이지인지 여부"),
                                fieldWithPath("data.totalPages").
                                        description("전체 페이지 개수"),
                                fieldWithPath("data.totalElements").
                                        description("테이블 총 데이터 개수"),
                                fieldWithPath("data.first").
                                        description("첫번째 페이지인지 여부"),
                                fieldWithPath("data.numberOfElements").
                                        description("요청 페이지에서 조회 된 데이터 개수"),
                                fieldWithPath("data.number").
                                        description("현재 페이지 번호"),
                                fieldWithPath("data.size").
                                        description("한 페이지당 조회할 데이터 개수"),

                                fieldWithPath("data.sort.sorted").
                                        description("정렬 됐는지 여부"),
                                fieldWithPath("data.sort.unsorted").
                                        description("정렬 안 됐는지 여부"),
                                fieldWithPath("data.sort.empty").
                                        description("데이터가 비었는지 여부"),

                                fieldWithPath("data.empty").
                                        description("데이터가 비었는지 여부"),

                                fieldWithPath("data.pageable").
                                        description("페이징 정보")
                        )
                ));
    }

    private Authentication getAuthentication() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("1");
        return authentication;
    }

    private NegotiationResponse createNegotiationResponse(final Long id, final String username, final int price, final long itemId) {
        return NegotiationResponse.builder()
                .id(id)
                .status(SUGGEST)
                .username(username)
                .itemId(itemId)
                .suggestedPrice(price)
                .build();
    }
}
