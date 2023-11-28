package com.example.market.dto.user.request;

import com.example.market.domain.entity.user.Coordinate;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserUpdateCoordinateRequest {

    @NotNull(message = "좌표는 필수로 입력해야 합니다.")
    private Coordinate coordinate;

    @Builder
    public UserUpdateCoordinateRequest(final Coordinate coordinate) {
        this.coordinate = coordinate;
    }

}
