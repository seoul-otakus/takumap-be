package com.seoulotakus.takumapbe.domain.review.dto.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WriterDTO {
    private final Long id;
    private final String nickname;

    public static WriterDTO of(Long id, String nickname) {
        return new WriterDTO(id, nickname);
    }
}
