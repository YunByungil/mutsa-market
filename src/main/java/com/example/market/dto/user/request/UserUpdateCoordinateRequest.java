package com.example.market.dto.user.request;

import com.example.market.domain.entity.user.Coordinate;
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

    private Coordinate coordinate;

    @Builder
    public UserUpdateCoordinateRequest(final Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    private static Point createPoint(final Coordinate coordinate) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(coordinate.getLat(), coordinate.getLng()));
    }

}
