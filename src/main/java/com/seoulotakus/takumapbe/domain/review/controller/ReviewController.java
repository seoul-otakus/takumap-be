package com.seoulotakus.takumapbe.domain.review.controller;

import com.seoulotakus.takumapbe.domain.review.dto.request.CreateReview;
import com.seoulotakus.takumapbe.domain.review.dto.request.UpdateReview;
import com.seoulotakus.takumapbe.domain.review.dto.response.ReadReview;
import com.seoulotakus.takumapbe.domain.review.service.ReviewService;
import com.seoulotakus.takumapbe.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReadReview>> createReview(@RequestBody CreateReview req) {
        ReadReview res = reviewService.createReview(req);
        return ResponseEntity.ok(ApiResponse.success(res, "리뷰 등록 성공"));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReadReview>> getReview(@PathVariable("reviewId") Long reviewId) {
        ReadReview res = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(ApiResponse.success(res, "리뷰 상세 조회 성공"));
    }

    @GetMapping("/{shopId}")
    public ResponseEntity<ApiResponse<Page<ReadReview>>> getReviewsByShopId(
            @PathVariable("shopId") Long shopId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReadReview> res = reviewService.getReviewsByShopId(shopId, pageable);
        return ResponseEntity.ok(ApiResponse.success(res,"리뷰 목록 조회 성공"));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReadReview>> updateReview(
            @PathVariable Long reviewId,
            @RequestBody UpdateReview req) {
        ReadReview res = reviewService.updateReviewById(reviewId, req);
        return ResponseEntity.ok(ApiResponse.success(res, "리뷰 수정 성공"));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId) {
        reviewService.deleteReviewById(reviewId);
        return ResponseEntity.ok(ApiResponse.success(null, "리뷰 삭제 성공"));
    }
}
