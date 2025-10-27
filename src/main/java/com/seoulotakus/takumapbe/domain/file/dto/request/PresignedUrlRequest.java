package com.seoulotakus.takumapbe.domain.file.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class PresignedUrlRequest {
    @NotEmpty
    @Valid
    private List<FileMetadataRequest> fileMetadataList;
}
