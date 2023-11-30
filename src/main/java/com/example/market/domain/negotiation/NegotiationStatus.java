package com.example.market.domain.negotiation;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Getter
public enum NegotiationStatus {

    SUGGEST("제안"), REJECT("거절"), ACCEPT("수락"), CONFIRM("확정");

    private String status;
    NegotiationStatus(String status) {
        this.status = status;
    }

    public static NegotiationStatus findByNegotiationStatus(String status) {
        return Arrays.stream(NegotiationStatus.values())
                .filter(negotiationStatus -> negotiationStatus.hasStatus(status))
                .findAny()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    public boolean hasStatus(String status) {
        return getStatus().equals(status);
    }
}
