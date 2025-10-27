package com.seoulotakus.takumapbe.domain.file.controller;

import com.seoulotakus.takumapbe.domain.file.dto.request.PresignedUrlRequest;
import com.seoulotakus.takumapbe.domain.file.dto.response.PresignedUrlResponse;
import com.seoulotakus.takumapbe.domain.file.service.FileMetadataService;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.global.response.ApiResponse;
import com.seoulotakus.takumapbe.global.util.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileMetadataController {
    private final FileMetadataService fileMetadataService;

    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> createPresignedUrls(
            @Valid @RequestBody PresignedUrlRequest request,
            @CurrentUser UserEntity user
            ){
        PresignedUrlResponse response = fileMetadataService.generatePresignedUrls(request, user);

        return ResponseEntity.ok(ApiResponse.success(response, "Presigned URL 생성 성공"));
    }
}
