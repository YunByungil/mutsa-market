package com.example.market.api.controller.item.request;

import com.example.market.domain.item.ItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemStatusUpdateRequest {

    @NotNull(message = "상품 판매상태는 필수입니다.")
    private ItemStatus status;

    @Builder
    public ItemStatusUpdateRequest(final ItemStatus status) {
        this.status = status;
    }
}
