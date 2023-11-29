package com.example.market.service.user;

import com.example.market.domain.entity.user.Coordinate;
import com.example.market.domain.entity.user.SearchScope;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.user.request.UserCreateRequestDto;
import com.example.market.dto.user.request.UserUpdateCoordinateRequest;
import com.example.market.dto.user.request.UserUpdateSearchScopeRequest;
import com.example.market.dto.user.response.UserCreateResponseDto;
import com.example.market.dto.user.response.UserResponse;
import com.example.market.exception.ErrorCode;
import com.example.market.exception.MarketAppException;
import com.example.market.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.market.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserCreateRequestDto dto) {
        validateDuplicateUsername(dto.getUsername());
        validateLatAndLng(dto.getCoordinate());

        User user = dto.toEntity(passwordEncoder.encode(dto.getPassword()));
        User savedUser = userRepository.save(user);

        return UserResponse.of(savedUser);
    }

    public UserResponse updateCoordinate(final Long userId, final UserUpdateCoordinateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

        validateLatAndLng(request.getCoordinate());

        user.updateCoordinate(createPoint(request.getCoordinate()));

        return UserResponse.of(user);
    }

    public UserResponse updateSearchScope(final Long userId, final UserUpdateSearchScopeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

        user.updateSearchScope(request.getSearchScope());

        return UserResponse.of(user);
    }

    private void validateDuplicateUsername(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new MarketAppException(ALREADY_USER_USERNAME, ALREADY_USER_USERNAME.getMessage());
                });
    }

    private void validateLatAndLng(final Coordinate coordinate) {
        if (coordinate.getLat() == null || coordinate.getLng() == null) {
            throw new MarketAppException(NOT_FOUND_COORDINATE, NOT_FOUND_COORDINATE.getMessage());
        }
    }

    private static Point createPoint(final Coordinate coordinate) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(coordinate.getLat(), coordinate.getLng()));
    }
}
