package com.example.market.dto.negotiation.request;

import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.Negotiation;
import com.example.market.domain.entity.enums.NegotiationStatus;
import com.example.market.domain.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NegotiationCreateRequestDto {

    @NotNull(message = "제안 상태는 필수입니다.")
    private NegotiationStatus status;

    @Positive(message = "가격은 양수여야 합니다.")
    private int suggestedPrice;

    @Builder
    public NegotiationCreateRequestDto(final NegotiationStatus status, final int suggestedPrice) {
        this.status = status;
        this.suggestedPrice = suggestedPrice;
    }

    public Negotiation toEntity(final Item item, final User buyer, final User seller) {
        return Negotiation.builder()
                .item(item)
                .buyer(buyer)
                .seller(seller)
                .status(NegotiationStatus.SUGGEST)
                .suggestedPrice(suggestedPrice)
                .build();
    }
}
