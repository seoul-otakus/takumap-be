package com.seoulotakus.takumapbe.domain.review.service;

import com.seoulotakus.takumapbe.domain.review.dto.request.CreateReview;
import com.seoulotakus.takumapbe.domain.review.dto.request.UpdateReview;
import com.seoulotakus.takumapbe.domain.review.dto.response.ReadReview;
import com.seoulotakus.takumapbe.domain.review.entity.Review;
import com.seoulotakus.takumapbe.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;


    @Transactional
    public ReadReview createReview(CreateReview req) {
        Review review = Review.builder()
                .userId(req.getUserId())
                .shopId(req.getShopId())
                .rating(req.getRating())
                .content(req.getContent())
                .build();

        Review savedReview = reviewRepository.save(review);
        return ReadReview.fromEntity(savedReview);
    }

    public ReadReview getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(); //TODO. 예외처리

        return ReadReview.fromEntity(review);
    }

    public Page<ReadReview> getReviewsByShopId(Long shopId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByShopId(shopId, pageable);

        return reviews.map(ReadReview::fromEntity);
    }

    @Transactional
    public ReadReview updateReviewById(Long reviewId, UpdateReview req) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(); // TODO. 예외처리

        review.update(req.getRating(), req.getContent());

        return ReadReview.fromEntity(review);
    }

    @Transactional
    public void deleteReviewById(Long reviewId) {

        if (!reviewRepository.existsById(reviewId)) {
            // throw new //TODO. 예외처리
        }

        reviewRepository.deleteById(reviewId);
    }

}
