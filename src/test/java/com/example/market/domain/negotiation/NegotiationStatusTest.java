package com.example.market.domain.negotiation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class NegotiationStatusTest {

    @DisplayName("NegotiationStatus Enum 클래스 테스트")
    @Test
    void statusEnum() {
        System.out.println(NegotiationStatus.CONFIRM); // CONFIRM

        System.out.println(NegotiationStatus.CONFIRM.getStatus()); // 확정

        System.out.println(NegotiationStatus.CONFIRM.name()); // CONFIRM

        System.out.println(NegotiationStatus.values().length); // 4

        for (NegotiationStatus value : NegotiationStatus.values()) {
            System.out.println("value = " + value);
        }
    }

    @DisplayName("Status Enum 클래스 안에 메서드 테스트")
    @Test
    void testMethodInEnum() {
        System.out.println(NegotiationStatus.findByNegotiationStatus(NegotiationStatus.ACCEPT.getStatus()));

        System.out.println(NegotiationStatus.findByNegotiationStatus("확정").getStatus());

        System.out.println(NegotiationStatus.findByNegotiationStatus("거절"));

    }
}