package com.seoulotakus.takumapbe.domain.file.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PresignedUrlResponse {
    private final List<PresignedUrl> presignedUrls;
}
