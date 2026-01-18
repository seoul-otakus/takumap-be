package com.seoulotakus.takumapbe.domain.review.controller;

import com.seoulotakus.takumapbe.domain.review.dto.response.BackofficeReviewResponse;
import com.seoulotakus.takumapbe.domain.review.service.ReviewService;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.global.response.ApiResponse;
import com.seoulotakus.takumapbe.global.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/backoffice/reviews")
public class BackofficeReviewController {

    private final ReviewService reviewService;

    /**
     * 내 가게 리뷰 목록 조회
     * [Check 1] ROLE_BOSS 권한 확인 (Service 내부)
     * [Check 2] 본인 소유 가게의 리뷰만 조회 (Repository Query + JWT ID)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BackofficeReviewResponse>>> getMyShopReviews(
            @CurrentUser UserEntity user,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<BackofficeReviewResponse> response = reviewService.getReviewForManager(user, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "내 가게 리뷰 목록 조회 성공"));
    }

    /**
     * 리뷰 삭제 (사장님 권한)
     * [Check 1] ROLE_BOSS 권한 확인 (Service 내부)
     * [Check 2] 해당 리뷰가 내 가게의 리뷰인지 확인 (Service 내부 Java Logic)
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReviewByManager(
            @PathVariable Long reviewId,
            @CurrentUser UserEntity user
    ) {
        reviewService.deleteReviewByManager(reviewId, user);
        return ResponseEntity.ok(ApiResponse.success(null, "리뷰 삭제(관리) 성공"));
    }
}
