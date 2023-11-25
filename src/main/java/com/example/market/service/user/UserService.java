package com.example.market.service.user;

import com.example.market.domain.entity.user.Coordinate;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.user.request.UserCreateRequestDto;
import com.example.market.dto.user.response.UserCreateResponseDto;
import com.example.market.dto.user.response.UserResponse;
import com.example.market.exception.ErrorCode;
import com.example.market.exception.MarketAppException;
import com.example.market.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
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
}
