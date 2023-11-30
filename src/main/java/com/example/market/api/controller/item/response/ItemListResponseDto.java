package com.example.market.api.controller.item.response;

import com.example.market.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemListResponseDto {

    private Long id;
    private String title;
    private String description;
    private int minPriceWanted;
    private String imageUrl;
    private String status;

    public ItemListResponseDto(Item item) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.minPriceWanted = item.getMinPriceWanted();
        this.imageUrl = item.getImageUrl();
        this.status = item.getStatus().getStatus();
    }
}
