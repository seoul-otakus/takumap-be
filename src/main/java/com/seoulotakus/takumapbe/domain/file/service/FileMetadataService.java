package com.seoulotakus.takumapbe.domain.file.service;

import com.seoulotakus.takumapbe.domain.file.dto.request.PresignedUrlRequest;
import com.seoulotakus.takumapbe.domain.file.dto.response.PresignedUrl;
import com.seoulotakus.takumapbe.domain.file.dto.response.PresignedUrlResponse;
import com.seoulotakus.takumapbe.domain.file.entity.FileMetadata;
import com.seoulotakus.takumapbe.domain.file.repository.FileMetadataRepository;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileMetadataService {
    private final FileMetadataRepository fileMetadataRepository;
    private final S3Service s3Service;

    private static final String REVIEW_IMAGE_PATH = "reviews/";

    @Transactional
    public PresignedUrlResponse generatePresignedUrls(
            PresignedUrlRequest request,
            UserEntity user
    ) {
        log.info("사용자 {} {}개 presigned URL 생성 요청", user.getId(), request.getFileMetadataList().size());

        List<PresignedUrl> presignedUrls = request.getFileMetadataList().stream()
                .map(fileMetadataRequest -> {
                    // 1. 고유한 Object Key 생성
                    String objectKey = REVIEW_IMAGE_PATH + UUID.randomUUID() + "-" + fileMetadataRequest.getFilename();

                    // 2. S3 Presigned URL (PUT) 생성
                    String uploadUrl = s3Service.getUploadUrl(objectKey, fileMetadataRequest.getMimeType());

                    // 3. FileMetadata 엔티티 생성 및 저장 (아직 리뷰 연결 X)
                    FileMetadata fileMetadata = FileMetadata.builder()
                            .fileName(fileMetadataRequest.getFilename())
                            .mimeType(fileMetadataRequest.getMimeType())
                            .objectKey(objectKey)
                            .createdBy(user)
                            .build();
                    fileMetadataRepository.save(fileMetadata);

                    return new PresignedUrl(
                            fileMetadataRequest.getFilename(),
                            uploadUrl,
                            objectKey
                    );
                })
                .collect(Collectors.toList());

        return new PresignedUrlResponse(presignedUrls);
    }
}
