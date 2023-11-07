package com.example.market.dto.negotiation.response;

import com.example.market.domain.entity.Negotiation;
import com.example.market.domain.entity.enums.NegotiationStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NegotiationResponse {

    private Long id;
    private int suggestedPrice;
    private NegotiationStatus status;
    private String username;
    private Long itemId;

    @Builder
    public NegotiationResponse(final Long id, final int suggestedPrice, final NegotiationStatus status, final String username, final Long itemId) {
        this.id = id;
        this.suggestedPrice = suggestedPrice;
        this.status = status;
        this.username = username;
        this.itemId = itemId;
    }

    public static NegotiationResponse of(final Negotiation negotiation) {
        return NegotiationResponse.builder()
                .id(negotiation.getId())
                .suggestedPrice(negotiation.getSuggestedPrice())
                .status(negotiation.getStatus())
                .username(negotiation.getBuyer().getUsername())
                .itemId(negotiation.getItem().getId())
                .build();
    }
}
