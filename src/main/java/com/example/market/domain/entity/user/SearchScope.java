package com.example.market.domain.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchScope {

    NARROW(35000.0),
    NORMAL(200000.0),
    WIDE(400000.0);

    private final Double scope;
}
