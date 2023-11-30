package com.example.market.service.negotiation;

import com.example.market.api.controller.negotiation.request.NegotiationCreateRequestDto;
import com.example.market.domain.item.Item;
import com.example.market.domain.negotiation.Negotiation;
import com.example.market.domain.item.ItemStatus;
import com.example.market.domain.user.User;
import com.example.market.api.controller.negotiation.response.NegotiationResponse;
import com.example.market.exception.MarketAppException;
import com.example.market.domain.item.ItemRepository;
import com.example.market.domain.negotiation.NegotiationRepository;
import com.example.market.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.example.market.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NegotiationService {

    private final NegotiationRepository negotiationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public NegotiationResponse createNegotiation(final Long itemId, final NegotiationCreateRequestDto request, final Long buyerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        validateDuplicateNegotiation(itemId, buyerId);

        validateItemStatusIsSold(item);

        validateCannotNegotiateOwnItem(buyerId, item);

        Negotiation negotiation = negotiationRepository.save(request.toEntity(item, buyer, item.getUser()));

        return NegotiationResponse.of(negotiation);
    }

    public Page<NegotiationResponse> getReceivedNegotiationItems(final Long userId, final int page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));
        Page<Negotiation> negotiations = negotiationRepository.
                findAllBySellerId(userId, PageRequest.of(page, 20, Sort.by("id").ascending()));

        return negotiations.map(NegotiationResponse::of);
    }

    public Page<NegotiationResponse> getSentNegotiationItems(final Long userId, final int page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));
        Page<Negotiation> negotiations = negotiationRepository.
                findAllByBuyerId(userId, PageRequest.of(page, 20, Sort.by("id").ascending()));

        return negotiations.map(NegotiationResponse::of);
    }

//    @Transactional
//    public NegotiationResponseDto updateNegotiation(Long itemId, Long negotiationId, NegotiationUpdateRequestDto updateDto, Long userId) {
//        Item item = itemRepository.findById(itemId)
//                .orElseThrow(() -> new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));
//
//        Negotiation negotiation = negotiationRepository.findById(negotiationId)
//                .orElseThrow(() -> new MarketAppException(NOT_FOUND_NEGOTIATION, NOT_FOUND_NEGOTIATION.getMessage()));
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//
//        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//    }

    private void validateCannotNegotiateOwnItem(final Long buyerId, final Item item) {
        if (item.getUser().getId().equals(buyerId)) {
            throw new MarketAppException(CANNOT_NEGOTIATION_OWN_ITEM, CANNOT_NEGOTIATION_OWN_ITEM.getMessage());
        }
    }

    private void validateItemStatusIsSold(final Item item) {
        if (item.getStatus().equals(ItemStatus.SOLD)) {
            throw new MarketAppException(ALREADY_ITEM_SOLD, ALREADY_ITEM_SOLD.getMessage());
        }
    }

    private void validateDuplicateNegotiation(final Long itemId, final Long buyerId) {
        negotiationRepository.findByItemIdAndBuyerId(itemId, buyerId)
                .ifPresent(negotiation -> {
                    throw new MarketAppException(ALREADY_USER_NEGOTIATION, ALREADY_USER_NEGOTIATION.getMessage());
                });
    }
}
