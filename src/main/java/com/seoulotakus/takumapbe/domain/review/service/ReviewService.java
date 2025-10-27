package com.seoulotakus.takumapbe.domain.review.service;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.seoulotakus.takumapbe.domain.file.entity.FileMetadata;
import com.seoulotakus.takumapbe.domain.file.repository.FileMetadataRepository;
import com.seoulotakus.takumapbe.domain.review.dto.common.ImageDTO;
import com.seoulotakus.takumapbe.domain.review.dto.request.ReviewCreateRequest;
import com.seoulotakus.takumapbe.domain.review.dto.request.ReviewUpdateRequest;
import com.seoulotakus.takumapbe.domain.review.dto.response.ReviewDetailResponse;
import com.seoulotakus.takumapbe.domain.review.dto.response.ReviewSingleResponse;
import com.seoulotakus.takumapbe.domain.review.entity.Review;
import com.seoulotakus.takumapbe.domain.review.repository.ReviewRepository;
import com.seoulotakus.takumapbe.domain.shop.entity.Shop;
import com.seoulotakus.takumapbe.domain.shop.repository.ShopRepository;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.global.exception.BusinessException;
import com.seoulotakus.takumapbe.global.exception.ErrorCode;
import com.seoulotakus.takumapbe.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ShopRepository shopRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final S3Service s3Service;

    @Transactional
    public ReviewDetailResponse createReview(ReviewCreateRequest request, UserEntity user) {
        log.info("리뷰 생성 - 샵 ID: {} 사용자 ID: {}", request.getShopId(), user.getId());

        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        Review review = Review.builder()
                .writer(user)
                .shop(shop)
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        Review savedReview = reviewRepository.save(review);

        linkFilesToReview(savedReview, request.getObjectKeys(), user);

        return buildDetailResponse(savedReview);
    }

    public ReviewDetailResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findByIdWithDetails(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        return buildDetailResponse(review);
    }

    public Page<ReviewSingleResponse> getReviewsByShopId(Long shopId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByShopIdWithWriter(shopId, pageable);

        return reviews.map(review -> {
            // 목록에서는 썸네일 (첫번째 이미지) 1장만 조회
            List<ImageDTO> images = getReviewImages(review, 1);
            return new ReviewSingleResponse(review, images);
        });
    }

    @Transactional
    public ReviewDetailResponse updateReview(Long reviewId, ReviewUpdateRequest request, UserEntity user) {
        log.info("업데이트 시도 리뷰 ID : {} 수정자 : {}", reviewId, user.getId());

        Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        // 1. 작성자 인증
        checkWriter(review, user);

        // 2. 텍스트 업데이트
        review.update(request.getRating(), request.getContent(), user);

        // 3. 파일 연결 (추가)
        linkFilesToReview(review, request.getObjectKeysToAdd(), user);

        // 4. 파일 연결 해제 (삭제)
        unlinkFilesFromReview(review, request.getObjectKeysToRemove());

        Review updatedReview = reviewRepository.save(review);

        return buildDetailResponse(updatedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId, UserEntity user) {
        log.info("삭제 시도 리뷰 ID : {} 사용자 ID : {}", reviewId, user.getId());

        Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        // 작성자 확인
        checkWriter(review, user);

        // Soft Delete
        review.softDelete(user);
    }

    //== Helper Methods ==//

    /**
     * FileMetadata 를 Review 에 연결
     */
    private void linkFilesToReview(Review review, List<String> objectKeys, UserEntity user) {
        if (CollectionUtils.isEmpty(objectKeys)) return;

        List<FileMetadata> filesToLink = fileMetadataRepository.findByObjectKeyIn(objectKeys);
        for (FileMetadata file : filesToLink) {
            if (file.getReview() == null) {
                file.linkToReview(review, user);
            } else {
                log.warn("해당 파일 {} 은 이미 다른 리뷰에 등록되어 있습니다. {} ", file.getObjectKey(), file.getReview().getId());
                throw new BusinessException(ErrorCode.INVALID_REQUEST);
            }
        }
    }

    /**
     * FileMetadata 과 Review 의 연결 해제 (Soft Delete)
     */
    private void unlinkFilesFromReview(Review review, List<String> objectKeysToRemove) {
        if (CollectionUtils.isNotEmpty(objectKeysToRemove)) return;

        // 1. 리뷰에 연결된 파일 중, 삭제할 objectKey를 가진 파일들을 조회
        List<FileMetadata> files = fileMetadataRepository.findByReview(review);
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
    private List<ImageDTO> getReviewImages(Review review, int limit) {
        List<FileMetadata> images = fileMetadataRepository.findByReview(review);

        return images.stream()
                .limit(limit > 0 ? limit : Long.MAX_VALUE) // limit (0 이하면 전체)
                .map(file -> ImageDTO.of(
                        file.getObjectKey(),
                        s3Service.getDownloadPresignedUrl(file.getObjectKey())
                ))
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 엔티티로 상세 응답 DTO를 생성 (모든 이미지 URL 포함)
     */
    private ReviewDetailResponse buildDetailResponse(Review review) {
        List<ImageDTO> images = getReviewImages(review, 0);
        return new ReviewDetailResponse(review, images);
    }

    private void checkWriter(Review review, UserEntity user) {
        if (!review.getWriter().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

}
