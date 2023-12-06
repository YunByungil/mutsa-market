package com.example.market.domain.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public enum ItemStatus {
    SALE("판매중"), RESERVATION("예약중"), SOLD("판매완료");

    private final String status;

    public static List<ItemStatus> forDisplay() {
        return List.of(SALE, RESERVATION);
    }

    public static ItemStatus forSold() {
        return SOLD;
    }
}
