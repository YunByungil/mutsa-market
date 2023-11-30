package com.example.market.dto.user.request;

import com.example.market.domain.entity.enums.Role;
import com.example.market.domain.entity.user.Address;
import com.example.market.domain.entity.user.Coordinate;
import com.example.market.domain.entity.user.User;
import jakarta.validation.constraints.NotBlank;
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
public class UserCreateRequestDto {
    @NotBlank(message = "아이디는 필수로 입력해야 됩니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수로 입력해야 됩니다.")
    private String password;

    private String phoneNumber;
    private String email;
    private String nickname;

    @NotBlank(message = "주소는 필수로 입력해야 됩니다.")
    private String address;

    private String userImage;

    @NotNull(message = "좌표는 필수로 입력해야 됩니다.")
    private Coordinate coordinate;

    @Builder
    public UserCreateRequestDto(String username, String password, String phoneNumber, String email,
                                String nickname, final String address, String userImage, final Coordinate coordinate) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.nickname = nickname;
        this.address = address;
        this.userImage = userImage;
        this.coordinate = coordinate;
    }

    public User toEntity(final String password) {
        return User.builder()
                .username(username)
                .password(password)
                .phoneNumber(phoneNumber)
                .email(email)
                .nickname(nickname)
                .address(address)
                .userImage(userImage)
                .role(Role.USER)
                .location(createPoint(coordinate))
                .build();
    }

    private static Point createPoint(final Coordinate coordinate) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(coordinate.getLng(), coordinate.getLat()));
    }
}
