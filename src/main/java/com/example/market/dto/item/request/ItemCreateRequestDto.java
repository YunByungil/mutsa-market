package com.example.market.dto.item.request;

import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.enums.ItemStatus;
import com.example.market.domain.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @Positive(message = "가격은 양수여야 합니다.")
    private int minPriceWanted;

    private ItemStatus status;

    @Builder
    public ItemCreateRequestDto(String title, String description, int minPriceWanted, final ItemStatus status) {
        this.title = title;
        this.description = description;
        this.minPriceWanted = minPriceWanted;
        this.status = status == null ? ItemStatus.SALE : status;
    }

    public Item toEntity(User user) {
        return Item.builder()
                .title(title)
                .description(description)
                .minPriceWanted(minPriceWanted)
                .status(status)
                .user(user)
                .build();
    }
}
