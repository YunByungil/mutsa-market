package com.example.market.docs.item;

import com.example.market.api.controller.item.ItemController;
import com.example.market.api.controller.item.request.ItemStatusUpdateRequest;
import com.example.market.docs.RestDocsSupport;
import com.example.market.api.controller.item.request.ItemCreateRequestDto;
import com.example.market.api.controller.item.request.ItemUpdateRequestDto;
import com.example.market.api.controller.item.response.ItemResponse;
import com.example.market.domain.item.ItemStatus;
import com.example.market.service.item.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

import java.util.List;

import static com.example.market.domain.item.ItemStatus.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ItemControllerDocsTest extends RestDocsSupport {

    private final ItemService itemService = mock(ItemService.class);

    @Override
    protected Object initController() {
        return new ItemController(itemService);
    }

    @DisplayName("판매 상품 등록 API")
    @Test
    void createItem() throws Exception {
        Authentication authentication = getAuthentication();

        ItemCreateRequestDto request = ItemCreateRequestDto.builder()
                .title("상품이름")
                .description("상품설명")
                .minPriceWanted(10_000)
                .status(SALE)
                .build();

        given(itemService.create(any(ItemCreateRequestDto.class), anyLong()))
                .willReturn(ItemResponse.builder()
                        .id(1L)
                        .status(SALE)
                        .username("판매자")
                        .title("상품이름")
                        .description("상품설명")
                        .minPriceWanted(10_000)
                        .build());

        mockMvc.perform(
                        post("/items")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("item-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(STRING)
                                        .description("상품이름"),
                                fieldWithPath("description").type(STRING)
                                        .description("상품설명"),
                                fieldWithPath("minPriceWanted").type(NUMBER)
                                        .description("상품가격"),
                                fieldWithPath("status").type(STRING)
                                        .description("판매상태")
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
                                        .description("상품 ID"),
                                fieldWithPath("data.title").type(STRING)
                                        .description("상품제목"),
                                fieldWithPath("data.description").type(STRING)
                                        .description("상품설명"),
                                fieldWithPath("data.minPriceWanted").type(NUMBER)
                                        .description("상품가격"),
                                fieldWithPath("data.status").type(STRING)
                                        .description("판매상태"),
                                fieldWithPath("data.username").type(STRING)
                                        .description("판매자")
                        )
                ));
    }

    @DisplayName("판매상품 페이징 조회 API")
    @Test
    void readItemList() throws Exception {
        List<ItemResponse> list = List.of(
                createItemResponse(1L, "상품제목1", 10_000, "판매자", SALE),
                createItemResponse(2L, "상품제목2", 20_000, "판매자2", SALE),
                createItemResponse(3L, "상품제목3", 30_000, "판매자3", SALE)
        );

        Page<ItemResponse> result = new PageImpl<>(list);
        given(itemService.readItemList(anyInt(), anyInt()))
                .willReturn(result);

        mockMvc.perform(
                        get("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("item-read-list",
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
                                        .description("상품 ID"),
                                fieldWithPath("data.content[].title").type(STRING)
                                        .description("상품제목"),
                                fieldWithPath("data.content[].description").type(STRING)
                                        .description("상품설명"),
                                fieldWithPath("data.content[].minPriceWanted").type(NUMBER)
                                        .description("상품가격"),
                                fieldWithPath("data.content[].status").type(STRING)
                                        .description("판매상태"),
                                fieldWithPath("data.content[].username").type(STRING)
                                        .description("판매자"),

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

    @DisplayName("상품 단건 조회 API")
    @Test
    void readItemOne() throws Exception {
        given(itemService.readItemOne(anyLong()))
                .willReturn(createItemResponse(1L, "상품제목", 10_000, "판매자", SALE));

        mockMvc.perform(
                        get("/items/{itemId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("item-read-one",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
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
                                        .description("상품 ID"),
                                fieldWithPath("data.title").type(STRING)
                                        .description("상품제목"),
                                fieldWithPath("data.description").type(STRING)
                                        .description("상품설명"),
                                fieldWithPath("data.minPriceWanted").type(NUMBER)
                                        .description("상품가격"),
                                fieldWithPath("data.status").type(STRING)
                                        .description("판매상태"),
                                fieldWithPath("data.username").type(STRING)
                                        .description("판매자")

                        )
                ));
    }

    @DisplayName("상품 수정 API")
    @Test
    void updateItem() throws Exception {
        Authentication authentication = getAuthentication();

        ItemUpdateRequestDto request = ItemUpdateRequestDto.builder()
                .title("상품제목")
                .description("상품내용")
                .minPriceWanted(10_000)
                .build();

        given(itemService.updateItem(anyLong(), any(ItemUpdateRequestDto.class), anyLong()))
                .willReturn(updateItemResponse(request.getTitle(), request.getMinPriceWanted(), request.getDescription()));

        mockMvc.perform(
                        put("/items/{itemId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("item-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(STRING)
                                        .description("상품이름"),
                                fieldWithPath("description").type(STRING)
                                        .description("상품설명"),
                                fieldWithPath("minPriceWanted").type(NUMBER)
                                        .description("상품가격")
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
                                        .description("상품 ID"),
                                fieldWithPath("data.title").type(STRING)
                                        .description("상품제목"),
                                fieldWithPath("data.description").type(STRING)
                                        .description("상품설명"),
                                fieldWithPath("data.minPriceWanted").type(NUMBER)
                                        .description("상품가격"),
                                fieldWithPath("data.status").type(STRING)
                                        .description("판매상태"),
                                fieldWithPath("data.username").type(STRING)
                                        .description("판매자")
                        )
                ));
    }

    @DisplayName("상품 삭제 API")
    @Test
    void deleteItem() throws Exception {
        Authentication authentication = getAuthentication();

        given(itemService.deleteItem(anyLong(), anyLong()))
                .willReturn(deleteItemResponse("상품제목", 10_000, "상품설명"));

        mockMvc.perform(
                        delete("/items/{itemId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("item-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
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
                                        .description("상품 ID"),
                                fieldWithPath("data.title").type(STRING)
                                        .description("상품제목"),
                                fieldWithPath("data.description").type(STRING)
                                        .description("상품설명"),
                                fieldWithPath("data.minPriceWanted").type(NUMBER)
                                        .description("상품가격"),
                                fieldWithPath("data.status").type(STRING)
                                        .description("판매상태"),
                                fieldWithPath("data.username").type(STRING)
                                        .description("판매자")

                        )
                ));
    }

    @DisplayName("내 주변 판매 상품 페이징 조회 API")
    @Test
    void readItemListTest() throws Exception {
        Authentication authentication = getAuthentication();

        List<ItemResponse> list = List.of(
                createItemResponse(1L, "상품제목1", 10_000, "판매자", SALE),
                createItemResponse(2L, "상품제목2", 20_000, "판매자2", SALE),
                createItemResponse(3L, "상품제목3", 30_000, "판매자3", SALE)
        );

        Page<ItemResponse> result = new PageImpl<>(list);
        given(itemService.readItemListTest(anyLong(), anyInt(), anyInt()))
                .willReturn(result);

        mockMvc.perform(
                        get("/itemsTest")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("item-read-listTest",
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
                                        .description("상품 ID"),
                                fieldWithPath("data.content[].title").type(STRING)
                                        .description("상품제목"),
                                fieldWithPath("data.content[].description").type(STRING)
                                        .description("상품설명"),
                                fieldWithPath("data.content[].minPriceWanted").type(NUMBER)
                                        .description("상품가격"),
                                fieldWithPath("data.content[].status").type(STRING)
                                        .description("판매상태"),
                                fieldWithPath("data.content[].username").type(STRING)
                                        .description("판매자"),

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

    @DisplayName("상품 상태 수정 API")
    @Test
    void updateItemStatus() throws Exception {
        Authentication authentication = getAuthentication();

        ItemStatusUpdateRequest request = ItemStatusUpdateRequest.builder()
                .status(SOLD)
                .build();

        given(itemService.updateItemStatus(anyLong(), any(ItemStatusUpdateRequest.class), anyLong()))
                .willReturn(updateItemStatusResponse(request.getStatus()));

        mockMvc.perform(
                        put("/items/status/{itemId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("item-update-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("status").type(STRING)
                                        .description("상품 상태")
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
                                        .description("상품 ID"),
                                fieldWithPath("data.title").type(STRING)
                                        .description("상품제목"),
                                fieldWithPath("data.description").type(STRING)
                                        .description("상품설명"),
                                fieldWithPath("data.minPriceWanted").type(NUMBER)
                                        .description("상품가격"),
                                fieldWithPath("data.status").type(STRING)
                                        .description("판매상태"),
                                fieldWithPath("data.username").type(STRING)
                                        .description("판매자")

                        )
                ));
    }

    @DisplayName("내가 등록한 판매 중, 예약 중인 상품들 조회 API")
    @Test
    void readMyItemListForSale() throws Exception {
        Authentication authentication = getAuthentication();

        List<ItemResponse> list = List.of(
                createItemResponse(1L, "상품제목1", 10_000, "판매자", SALE),
                createItemResponse(2L, "상품제목2", 20_000, "판매자", SALE),
                createItemResponse(3L, "상품제목3", 30_000, "판매자", SALE),
                createItemResponse(4L, "상품제목4", 40_000, "판매자", RESERVATION)
        );

        Page<ItemResponse> result = new PageImpl<>(list);
        given(itemService.readMyItemListForSale(anyLong(), anyInt()))
                .willReturn(result);

        mockMvc.perform(
                        get("/items-sale")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("item-read-my-list-for-sale",
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
                                        .description("상품 ID"),
                                fieldWithPath("data.content[].title").type(STRING)
                                        .description("상품제목"),
                                fieldWithPath("data.content[].description").type(STRING)
                                        .description("상품설명"),
                                fieldWithPath("data.content[].minPriceWanted").type(NUMBER)
                                        .description("상품가격"),
                                fieldWithPath("data.content[].status").type(STRING)
                                        .description("판매상태"),
                                fieldWithPath("data.content[].username").type(STRING)
                                        .description("판매자"),

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

    @DisplayName("유저가 등록한 판매 중, 예약 중인 상품들 조회 API")
    @Test
    void readUserItemListForSale() throws Exception {
        Authentication authentication = getAuthentication();

        List<ItemResponse> list = List.of(
                createItemResponse(1L, "상품제목1", 10_000, "판매자", SALE),
                createItemResponse(2L, "상품제목2", 20_000, "판매자", SALE),
                createItemResponse(3L, "상품제목3", 30_000, "판매자", SALE),
                createItemResponse(4L, "상품제목4", 40_000, "판매자", RESERVATION)
        );

        Page<ItemResponse> result = new PageImpl<>(list);
        given(itemService.readUserItemListForSale(anyLong(), anyLong(), anyInt()))
                .willReturn(result);

        mockMvc.perform(
                        get("/items-sale/{userId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("item-read-user-list-for-sale",
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
                                        .description("상품 ID"),
                                fieldWithPath("data.content[].title").type(STRING)
                                        .description("상품제목"),
                                fieldWithPath("data.content[].description").type(STRING)
                                        .description("상품설명"),
                                fieldWithPath("data.content[].minPriceWanted").type(NUMBER)
                                        .description("상품가격"),
                                fieldWithPath("data.content[].status").type(STRING)
                                        .description("판매상태"),
                                fieldWithPath("data.content[].username").type(STRING)
                                        .description("판매자"),

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

    private ItemResponse createItemResponse(final Long id, final String title, final int minPriceWanted, final String username, final ItemStatus status) {
        return ItemResponse.builder()
                .id(id)
                .title(title)
                .description("상품내용")
                .status(status)
                .minPriceWanted(minPriceWanted)
                .username(username)
                .build();
    }

    private ItemResponse updateItemResponse(final String title, final int minPriceWanted, final String description) {
        return ItemResponse.builder()
                .id(1L)
                .title(title)
                .description(description)
                .status(SALE)
                .minPriceWanted(minPriceWanted)
                .username("판매자")
                .build();
    }

    private ItemResponse deleteItemResponse(final String title, final int minPriceWanted, final String description) {
        return ItemResponse.builder()
                .id(1L)
                .title(title)
                .description(description)
                .status(SALE)
                .minPriceWanted(minPriceWanted)
                .username("판매자")
                .build();
    }

    private ItemResponse updateItemStatusResponse(final ItemStatus status) {
        return ItemResponse.builder()
                .id(1L)
                .title("제목")
                .description("설명")
                .status(status)
                .minPriceWanted(10_000)
                .username("판매자")
                .build();
    }
}
