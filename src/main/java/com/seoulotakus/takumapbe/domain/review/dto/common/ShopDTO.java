package com.seoulotakus.takumapbe.domain.review.dto.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ShopDTO {
    private final Long id;
    private final String name;

    public static ShopDTO of(Long id, String name) {
        return new ShopDTO(id, name);
    }
}
