package com.seoulotakus.takumapbe.domain.review.dto.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageDTO {
    private final String objectKey;
    private final String downloadUrl;

    public static ImageDTO of(String objectKey, String downloadUrl) {
        return new ImageDTO(objectKey, downloadUrl);
    }
}
