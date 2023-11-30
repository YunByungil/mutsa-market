package com.example.market.api.controller.item.response;

import com.example.market.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemOneResponseDto {

    private Long id;
    private String title;
    private String description;
    private int minPriceWanted;
    private String status;

    public ItemOneResponseDto(Item item) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.minPriceWanted = item.getMinPriceWanted();
        this.status = item.getStatus().getStatus();
    }
}
