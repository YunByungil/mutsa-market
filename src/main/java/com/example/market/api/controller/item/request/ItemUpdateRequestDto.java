package com.example.market.api.controller.item.request;

import com.example.market.domain.item.Item;
import com.example.market.domain.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemUpdateRequestDto {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String description;

    @Positive(message = "가격은 양수여야 합니다.")
    private int minPriceWanted;

    @Builder
    public ItemUpdateRequestDto(String title, String description, int minPriceWanted) {
        this.title = title;
        this.description = description;
        this.minPriceWanted = minPriceWanted;
    }

    public Item toEntity(User user) {
        return Item.builder()
                .title(title)
                .description(description)
                .minPriceWanted(minPriceWanted)
                .user(user)
                .build();
    }
}
