package com.example.market.api.controller.negotiation;

import com.example.market.api.ApiResponse;
import com.example.market.dto.negotiation.request.NegotiationCreateRequestDto;
import com.example.market.dto.negotiation.request.NegotiationUpdateRequestDto;
import com.example.market.dto.negotiation.response.NegotiationListResponseDto;
import com.example.market.dto.negotiation.response.NegotiationResponse;
import com.example.market.dto.negotiation.response.NegotiationResponseDto;
import com.example.market.service.NegotiationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.market.common.SystemMessage.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class NegotiationController {

    private final NegotiationService negotiationService;

    @PostMapping("/items/{itemId}/proposals")
    public ApiResponse<NegotiationResponse> createNegotiation(@PathVariable Long itemId,
                                                              @Valid @RequestBody NegotiationCreateRequestDto createDto,
                                                              Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        return ApiResponse.ok(negotiationService.createNegotiation(itemId, createDto, userId));
    }

//    @GetMapping("/items/{itemId}/proposals")
//    public ApiResponse<Page<NegotiationListResponseDto>> readAllNegotiation(@PathVariable Long itemId,
//                                                               @RequestParam(value = "page", defaultValue = "0") int page,
//                                                               @RequestParam(value = "limit", defaultValue = "20") int limit,
//                                                               Authentication authentication) {
//        Long userId = Long.parseLong(authentication.getName());
//        return ApiResponse.ok(negotiationService.readAllNegotiation(itemId, page, limit, userId));
//    }

    @GetMapping("/items/received/proposals")
    public ApiResponse<Page<NegotiationResponse>> getReceivedNegotiationItems(final Authentication authentication,
                                                                              @RequestParam(value = "page", defaultValue = "1") int page) {
        Long userId = Long.parseLong(authentication.getName());
        return ApiResponse.ok(negotiationService.getReceivedNegotiationItems(userId, page));
    }

    @GetMapping("/items/sent/proposals")
    public ApiResponse<Page<NegotiationResponse>> getSentNegotiationItems(final Authentication authentication,
                                                                          @RequestParam(value = "page", defaultValue = "1") int page) {
        Long userId = Long.parseLong(authentication.getName());
        return ApiResponse.ok(negotiationService.getSentNegotiationItems(userId, page));
    }

//    @PutMapping("/items/{itemId}/proposals/{proposalId}")
//    public NegotiationResponseDto updateNegotiation(@PathVariable Long itemId,
//                                                    @PathVariable Long proposalId,
//                                                    @Valid @RequestBody NegotiationUpdateRequestDto updateDto,
//                                                    Authentication authentication) {
//        Long userId = Long.parseLong(authentication.getName());
//        return negotiationService.updateNegotiation(itemId, proposalId, updateDto, userId);
//    }

//    @DeleteMapping("/items/{itemId}/proposals/{proposalId}")
//    public NegotiationResponseDto deleteNegotiation(@PathVariable Long itemId,
//                                                    @PathVariable Long proposalId,
//                                                    Authentication authentication) {
//        Long userId = Long.parseLong(authentication.getName());
//        negotiationService.deleteNegotiation(itemId, proposalId, userId);
//
//        return new NegotiationResponseDto(DELETE_NEGOTIATION);
//    }
}
