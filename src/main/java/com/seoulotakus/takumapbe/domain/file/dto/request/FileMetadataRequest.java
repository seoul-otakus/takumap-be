package com.seoulotakus.takumapbe.domain.file.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FileMetadataRequest {
    @NotBlank(message = "파일 이름은 필수입니다")
    private String filename;

    @NotBlank(message = "MIME 타입은 필수입니다")
    private String mimeType;
}
