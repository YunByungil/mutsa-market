package com.example.market.api.controller.negotiation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NegotiationUpdateRequestDto {

    @PositiveOrZero
    private int suggestedPrice;
    private String status;

    @Builder
    public NegotiationUpdateRequestDto(int suggestedPrice, String status) {
        this.suggestedPrice = suggestedPrice;
        this.status = status;
    }
}
