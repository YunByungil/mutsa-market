package com.example.market.domain.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchScope {

    NARROW(35000.0), // 35KM
    NORMAL(200000.0), // 200KM
    WIDE(400000.0); // 400KM

    private final Double scope;
}
