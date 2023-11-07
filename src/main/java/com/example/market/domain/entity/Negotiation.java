package com.example.market.domain.entity;

import com.example.market.domain.entity.enums.NegotiationStatus;
import com.example.market.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Negotiation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int suggestedPrice;
    @Enumerated(EnumType.STRING)
    private NegotiationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @Builder
    public Negotiation(Item item, final User seller, final User buyer, int suggestedPrice, final NegotiationStatus status) {
        this.item = item;
        this.suggestedPrice = suggestedPrice;
        this.status = status;
        this.seller = seller;
        this.buyer = buyer;
    }

    public void updateNegotiation(int suggestedPrice) {
        this.suggestedPrice = suggestedPrice;
    }

    public void updateNegotiationStatus(NegotiationStatus status) {
        this.status = status;
    }
}
