package com.example.market.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
public class ErrorResponse {
    private String errorCode;
    private String message;
}
