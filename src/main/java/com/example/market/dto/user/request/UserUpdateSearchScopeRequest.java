package com.example.market.dto.user.request;

import com.example.market.domain.entity.user.SearchScope;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserUpdateSearchScopeRequest {

    @NotNull(message = "검색 범위는 필수로 입력해야 합니다.")
    private SearchScope searchScope;

    @Builder
    public UserUpdateSearchScopeRequest(final SearchScope searchScope) {
        this.searchScope = searchScope;
    }
}
