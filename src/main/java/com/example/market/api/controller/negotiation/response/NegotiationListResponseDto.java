package com.example.market.api.controller.negotiation.response;

import com.example.market.domain.negotiation.Negotiation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NegotiationListResponseDto {

    private Long id;
    private int suggestedPrice;
    private String status;

    public NegotiationListResponseDto(Negotiation negotiation) {
        this.id = negotiation.getId();
        this.suggestedPrice = negotiation.getSuggestedPrice();
        this.status = negotiation.getStatus().getStatus();
    }
}
