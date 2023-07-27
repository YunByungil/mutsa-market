package com.example.market.dto.item.request;

import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemCreateRequestDto {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String description;
    @PositiveOrZero(message = "가격을 입력해주세요.")
    private int minPriceWanted;

    @Builder
    public ItemCreateRequestDto(String title, String description, int minPriceWanted) {
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
