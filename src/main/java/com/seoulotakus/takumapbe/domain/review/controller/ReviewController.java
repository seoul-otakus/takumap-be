package com.seoulotakus.takumapbe.domain.review.controller;

import com.seoulotakus.takumapbe.domain.review.dto.request.CreateReview;
import com.seoulotakus.takumapbe.domain.review.dto.request.UpdateReview;
import com.seoulotakus.takumapbe.domain.review.dto.response.ReadReview;
import com.seoulotakus.takumapbe.domain.review.service.ReviewService;
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
    public ResponseEntity<ReadReview> createReview(@RequestBody CreateReview req) {
        ReadReview res = reviewService.createReview(req);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReadReview> getReview(@PathVariable("reviewId") Long reviewId) {
        ReadReview res = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{shopId}")
    public ResponseEntity<Page<ReadReview>> getReviewsByShopId(
            @PathVariable("shopId") Long shopId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReadReview> res = reviewService.getReviewsByShopId(shopId, pageable);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReadReview> updateReview(
            @PathVariable Long reviewId,
            @RequestBody UpdateReview req) {
        ReadReview updatedReview = reviewService.updateReviewById(reviewId, req);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId) {
        reviewService.deleteReviewById(reviewId);
        return ResponseEntity.noContent().build();
    }
}
