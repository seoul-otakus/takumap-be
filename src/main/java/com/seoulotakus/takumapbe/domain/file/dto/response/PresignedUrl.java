package com.seoulotakus.takumapbe.domain.file.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PresignedUrl {
    private final String filename;
    private final String uploadUrl;
    private final String objectKey;
}
