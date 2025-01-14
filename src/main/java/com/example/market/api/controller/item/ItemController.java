package com.example.market.api.controller.item;

import com.example.market.api.ApiResponse;
import com.example.market.api.controller.item.request.ItemCreateRequestDto;
import com.example.market.api.controller.item.request.ItemStatusUpdateRequest;
import com.example.market.api.controller.item.request.ItemUpdateRequestDto;
import com.example.market.api.controller.item.response.ItemResponse;
import com.example.market.service.item.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/items")
    public ApiResponse<ItemResponse> create(@Valid @RequestBody ItemCreateRequestDto dto,
                                            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        return ApiResponse.ok(itemService.create(dto, userId));
    }

    @GetMapping("/items")
    public ApiResponse<Page<ItemResponse>> readItemList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                        @RequestParam(value = "limit", defaultValue = "20") Integer limit) {

        return ApiResponse.ok(itemService.readItemList(page, limit));
    }

    @GetMapping("/itemsTest")
    public ApiResponse<Page<ItemResponse>> readItemListTest(final Authentication authentication,
                                                            @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @RequestParam(value = "limit", defaultValue = "20") Integer limit) {

        Long userId = Long.parseLong(authentication.getName());
        return ApiResponse.ok(itemService.readItemListTest(userId, page, limit));
    }

    @GetMapping("/items/{itemId}")
    public ApiResponse<ItemResponse> readItemOne(@PathVariable Long itemId) {

        return ApiResponse.ok(itemService.readItemOne(itemId));
    }

    @PutMapping("/items/{itemId}")
    public ApiResponse<ItemResponse> updateItem(@PathVariable Long itemId,
                                                @Valid @RequestBody ItemUpdateRequestDto dto,
                                                Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        return ApiResponse.ok(itemService.updateItem(itemId, dto, userId));
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<ItemResponse> deleteItem(@PathVariable Long itemId,
                                                Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        return ApiResponse.ok(itemService.deleteItem(itemId, userId));
    }

    //    @PutMapping("/items/{itemId}/image")
    @RequestMapping(value = "/items/{itemId}/image", method = {RequestMethod.POST, RequestMethod.PUT})
    public ApiResponse<ItemResponse> updateItemImage(@PathVariable Long itemId,
                                                     @RequestParam MultipartFile image,
                                                     Authentication authentication) throws IOException {
        Long userId = Long.parseLong(authentication.getName());
        return ApiResponse.ok(itemService.updateItemImage(itemId, image, userId));
    }

    @PutMapping("/items/status/{itemId}")
    public ApiResponse<ItemResponse> updateItemStatus(@PathVariable final Long itemId,
                                                      @Valid @RequestBody final ItemStatusUpdateRequest request,
                                                      final Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());
        return ApiResponse.ok(itemService.updateItemStatus(itemId, request, userId));
    }

    @GetMapping("/items-sale")
    public ApiResponse<Page<ItemResponse>> readMyItemListForSale(final Authentication authentication,
                                                                 @RequestParam(value = "page", defaultValue = "0") int page) {

        Long userId = Long.parseLong(authentication.getName());
        return ApiResponse.ok(itemService.readMyItemListForSale(userId, page));
    }

    @GetMapping("/items-sold")
    public ApiResponse<Page<ItemResponse>> readMyItemListForSold(final Authentication authentication,
                                                                 @RequestParam(value = "page", defaultValue = "0") int page) {

        Long userId = Long.parseLong(authentication.getName());
        return ApiResponse.ok(itemService.readMyItemListForSold(userId, page));
    }

    @GetMapping("/items-sale/{userId}")
    public ApiResponse<Page<ItemResponse>> readUserItemListForSale(final Authentication authentication,
                                                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                                                   @PathVariable final Long userId) {
        Long myId = Long.parseLong(authentication.getName());
        return ApiResponse.ok(itemService.readUserItemListForSale(userId, myId, page));
    }

    @GetMapping("/items-sold/{userId}")
    public ApiResponse<Page<ItemResponse>> readUserItemListForSold(final Authentication authentication,
                                                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                                                   @PathVariable final Long userId) {
        Long myId = Long.parseLong(authentication.getName());
        return ApiResponse.ok(itemService.readUserItemListForSold(userId, myId, page));
    }
}
