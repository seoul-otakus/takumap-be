package com.seoulotakus.takumapbe.domain.review.controller;

import com.seoulotakus.takumapbe.domain.review.dto.request.ReviewCreateRequest;
import com.seoulotakus.takumapbe.domain.review.dto.request.ReviewUpdateRequest;
import com.seoulotakus.takumapbe.domain.review.dto.response.ReviewDetailResponse;
import com.seoulotakus.takumapbe.domain.review.dto.response.ReviewSingleResponse;
import com.seoulotakus.takumapbe.domain.review.service.ReviewService;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.global.response.ApiResponse;
import com.seoulotakus.takumapbe.global.util.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDetailResponse>> createReview(
            @Valid @RequestBody ReviewCreateRequest request,
            @CurrentUser UserEntity user
    ) {
        ReviewDetailResponse response = reviewService.createReview(request, user);
        return ResponseEntity.ok(ApiResponse.success(response, "리뷰 등록 성공"));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDetailResponse>> getReviewById(
            @PathVariable Long reviewId
    ) {
        ReviewDetailResponse response = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(ApiResponse.success(response, "리뷰 상세 조회 성공"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReviewSingleResponse>>> getReviewsByShopId(
            @RequestParam Long shopId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReviewSingleResponse> res = reviewService.getReviewsByShopId(shopId, pageable);
        return ResponseEntity.ok(ApiResponse.success(res,"리뷰 목록 조회 성공"));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDetailResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request,
            @CurrentUser UserEntity user) {
        ReviewDetailResponse response = reviewService.updateReview(reviewId, request, user);
        return ResponseEntity.ok(ApiResponse.success(response, "리뷰 수정 성공"));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @CurrentUser UserEntity user
    ) {
        reviewService.deleteReview(reviewId, user);
        return ResponseEntity.ok(ApiResponse.success(null, "리뷰 삭제 성공"));
    }
}
