package com.example.market.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum ItemStatus {
    SALE("판매중"), SOLD("판매완료");
    private final String status;

    public static ItemStatus forDisplay() {
        return SALE;
    }
}
