package com.example.market.domain.item;

import com.example.market.domain.comment.Comment;
import com.example.market.domain.negotiation.Negotiation;
import com.example.market.domain.user.User;
import com.example.market.api.controller.item.request.ItemUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//@Table(name = "sales_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private int minPriceWanted;
    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE)
    private List<Negotiation> negotiations = new ArrayList<>();

    @Builder
    public Item(String title, String description, String imageUrl, int minPriceWanted, User user, final ItemStatus status) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.minPriceWanted = minPriceWanted;
        this.status = status;
        this.user = user;
    }

    public void update(ItemUpdateRequestDto dto) {
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.minPriceWanted = dto.getMinPriceWanted();
    }

    public void updateItemImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateStatus(final ItemStatus status) {
        this.status = status;
    }
}
