package com.example.market.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coordinate {

    private Double lat;
    private Double lng;

    @Builder
    public Coordinate(final Double lat, final Double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
