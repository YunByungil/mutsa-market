package com.example.market.dto.item.response;

import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.enums.ItemStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemResponse {

    private Long id;
    private String title;
    private String description;
    private String username;
    private ItemStatus status;
    private int minPriceWanted;

    @Builder
    public ItemResponse(final Long id, final String title, final String description, final String username, final ItemStatus status, final int minPriceWanted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.username = username;
        this.status = status;
        this.minPriceWanted = minPriceWanted;
    }

    public static ItemResponse of(final Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .username(item.getUser().getUsername())
                .status(item.getStatus())
                .minPriceWanted(item.getMinPriceWanted())
                .build();
    }
}
