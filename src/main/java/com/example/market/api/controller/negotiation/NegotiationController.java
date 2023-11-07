package com.example.market.api.controller.negotiation;

import com.example.market.dto.negotiation.request.NegotiationCreateRequestDto;
import com.example.market.dto.negotiation.request.NegotiationUpdateRequestDto;
import com.example.market.dto.negotiation.response.NegotiationListResponseDto;
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
    public NegotiationResponseDto createNegotiation(@PathVariable Long itemId,
                                                    @Valid  @RequestBody NegotiationCreateRequestDto createDto,
                                                    Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        negotiationService.createNegotiation(itemId, createDto, userId);

        return new NegotiationResponseDto(REGISTER_NEGOTIATION);
    }

    @GetMapping("/items/{itemId}/proposals")
    public Page<NegotiationListResponseDto> readAllNegotiation(@PathVariable Long itemId,
                                                               @RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                               Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return negotiationService.readAllNegotiation(itemId, page, limit, userId);
    }

    @PutMapping("/items/{itemId}/proposals/{proposalId}")
    public NegotiationResponseDto updateNegotiation(@PathVariable Long itemId,
                                                    @PathVariable Long proposalId,
                                                    @Valid @RequestBody NegotiationUpdateRequestDto updateDto,
                                                    Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return negotiationService.updateNegotiation(itemId, proposalId, updateDto, userId);

    }

    @DeleteMapping("/items/{itemId}/proposals/{proposalId}")
    public NegotiationResponseDto deleteNegotiation(@PathVariable Long itemId,
                                                    @PathVariable Long proposalId,
                                                    Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        negotiationService.deleteNegotiation(itemId, proposalId, userId);

        return new NegotiationResponseDto(DELETE_NEGOTIATION);
    }
}
