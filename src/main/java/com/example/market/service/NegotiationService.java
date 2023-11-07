package com.example.market.service;

import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.Negotiation;
import com.example.market.domain.entity.enums.ItemStatus;
import com.example.market.domain.entity.enums.NegotiationStatus;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.negotiation.request.*;
import com.example.market.dto.negotiation.response.NegotiationListResponseDto;
import com.example.market.dto.negotiation.response.NegotiationResponse;
import com.example.market.dto.negotiation.response.NegotiationResponseDto;
import com.example.market.exception.MarketAppException;
import com.example.market.repository.ItemRepository;
import com.example.market.repository.NegotiationRepository;
import com.example.market.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.example.market.common.SystemMessage.*;
import static com.example.market.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NegotiationService {

    private final NegotiationRepository negotiationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public NegotiationResponse createNegotiation(Long itemId, NegotiationCreateRequestDto dto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));
        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Negotiation negotiation = negotiationRepository.save(dto.toEntity(item, buyer, item.getUser()));
        // TODO: 중복 신청 X 로직 추가

        return NegotiationResponse.of(negotiation);
    }

    public Page<NegotiationResponse> getReceivedNegotiationItems(final Long userId, final int page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));
        Page<Negotiation> negotiations = negotiationRepository.
                findAllBySellerId(userId, PageRequest.of(page - 1, 20, Sort.by("id").ascending()));

        return negotiations.map(NegotiationResponse::of);
    }

    public Page<NegotiationResponse> getSentNegotiationItems(final Long userId, final int page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));
        Page<Negotiation> negotiations = negotiationRepository.
                findAllBySellerId(userId, PageRequest.of(page - 1, 20, Sort.by("id").ascending()));

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
}
