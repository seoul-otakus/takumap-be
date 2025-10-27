package com.seoulotakus.takumapbe.domain.file.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class PresignedUrlRequest {
    @NotEmpty(message = "File Metadata는 비어있을 수 없습니다.")
    @Valid
    private List<FileMetadataRequest> fileMetadataList;
}
