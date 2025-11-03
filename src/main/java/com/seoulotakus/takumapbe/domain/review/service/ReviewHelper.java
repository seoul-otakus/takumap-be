package com.seoulotakus.takumapbe.domain.review.service;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.seoulotakus.takumapbe.domain.file.entity.FileMetadata;
import com.seoulotakus.takumapbe.domain.file.repository.FileMetadataRepository;
import com.seoulotakus.takumapbe.domain.review.dto.common.ImageDTO;
import com.seoulotakus.takumapbe.domain.review.dto.response.ReviewDetailResponse;
import com.seoulotakus.takumapbe.domain.review.entity.Review;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.global.exception.BusinessException;
import com.seoulotakus.takumapbe.global.exception.ErrorCode;
import com.seoulotakus.takumapbe.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewHelper {

    private final FileMetadataRepository fileMetadataRepository;
    private final S3Service s3Service;

    //== Helper Methods, Package-Private ==//

    /**
     * FileMetadata 를 Review 에 연결
     */
    public void linkFilesToReview(Review review, List<String> objectKeys, UserEntity user) {

        if (CollectionUtils.isEmpty(objectKeys)) return;

        List<FileMetadata> filesToLink = fileMetadataRepository.findByObjectKeyIn(objectKeys);
        for (FileMetadata file : filesToLink) {
            if (file.getTypeId() == null) {
                file.linkAssociation("REVIEW", review.getId(), user);
            } else {
                log.warn("해당 파일 {} 은 이미 다른 도메인에 등록되어 있습니다. Type: {}, ID: {} ", file.getObjectKey(), file.getType(), file.getTypeId());
                throw new BusinessException(ErrorCode.INVALID_REQUEST);
            }
        }
    }

    /**
     * FileMetadata 과 Review 의 연결 해제 (Soft Delete)
     */
    void unlinkFilesFromReview(Review review, List<String> objectKeysToRemove) {
        if (CollectionUtils.isEmpty(objectKeysToRemove)) return;

        // 1. 리뷰에 연결된 파일 중, 삭제할 objectKey를 가진 파일들을 조회
        List<FileMetadata> files = fileMetadataRepository.findByTypeAndTypeIdAndDeletedAtIsNull("REVIEW", review.getId());
        List<FileMetadata> filesToRemove = files.stream()
                .filter(img -> objectKeysToRemove.contains(img.getObjectKey()))
                .collect(Collectors.toList());

        // 2. 해당 파일들을 Soft Delete
        for (FileMetadata file : filesToRemove) {
            file.softDelete();
        }
    }

    /**
     * 리뷰 엔티티의 이미지 목록에 대한 Download Presigned URL을 생성
     */
    List<ImageDTO> getReviewImages(Review review, int limit) {
        List<FileMetadata> images = fileMetadataRepository.findByTypeAndTypeIdAndDeletedAtIsNull("REVIEW", review.getId());

        return images.stream()
                .limit(limit > 0 ? limit : Long.MAX_VALUE) // limit (0 이하면 전체)
                .map(file -> ImageDTO.of(
                        file.getObjectKey(),
                        s3Service.getDownloadPresignedUrl(file.getObjectKey())
                ))
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 엔티티로 상세 응답 DTO를 생성 (모든 이미지 URL 포함 d)
     */
    ReviewDetailResponse buildDetailResponse(Review review) {
        List<ImageDTO> images = getReviewImages(review, 0);
        return new ReviewDetailResponse(review, images);
    }

    // user.getId 타입 에러나서 아래 코드로 수정해서 올립니다. 빌드가 안되더라고요...
//    void checkWriter(Review review, UserEntity user) {
//        if (!review.getWriter().getId().equals(user.getId())) {
//            throw new BusinessException(ErrorCode.FORBIDDEN);
//        }
//    }

    void checkWriter(Review review, UserEntity user) {
        if (review.getWriter().getId() != user.getId()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
