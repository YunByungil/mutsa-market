package com.example.market.domain.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ItemStatus {
    SALE("판매중"), SOLD("판매완료");
    private final String status;

    public static ItemStatus forDisplay() {
        return SALE;
    }
}
