package com.seoulotakus.takumapbe.domain.review.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ShopRepository shopRepository;
    private final ReviewHelper helper;

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

        helper.linkFilesToReview(savedReview, request.getObjectKeys(), user);

        return helper.buildDetailResponse(savedReview);
    }

    public ReviewDetailResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findByIdWithDetails(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        return helper.buildDetailResponse(review);
    }

    public Page<ReviewSingleResponse> getReviewsByShopId(Long shopId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByShopIdWithWriter(shopId, pageable);

        return reviews.map(review -> {
            // 목록에서는 썸네일 (첫번째 이미지) 1장만 조회
            List<ImageDTO> images = helper.getReviewImages(review, 1);
            return new ReviewSingleResponse(review, images);
        });
    }

    @Transactional
    public ReviewDetailResponse updateReview(Long reviewId, ReviewUpdateRequest request, UserEntity user) {
        log.info("업데이트 시도 리뷰 ID : {} 수정자 : {}", reviewId, user.getId());

        Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        // 1. 작성자 인증
        helper.checkWriter(review, user);

        // 2. 텍스트 업데이트
        review.update(request.getRating(), request.getContent(), user);

        // 3. 파일 연결 (추가)
        helper.linkFilesToReview(review, request.getObjectKeysToAdd(), user);

        // 4. 파일 연결 해제 (삭제)
        helper.unlinkFilesFromReview(review, request.getObjectKeysToRemove());

        Review updatedReview = reviewRepository.save(review);

        return helper.buildDetailResponse(updatedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId, UserEntity user) {
        log.info("삭제 시도 리뷰 ID : {} 사용자 ID : {}", reviewId, user.getId());

        Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        // 작성자 확인
        helper.checkWriter(review, user);

        // Soft Delete
        review.softDelete(user);
    }

}
