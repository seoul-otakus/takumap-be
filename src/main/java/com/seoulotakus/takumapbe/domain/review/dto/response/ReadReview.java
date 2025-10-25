package com.seoulotakus.takumapbe.domain.review.dto.response;

import com.seoulotakus.takumapbe.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReadReview {
    private Long id;
    private Long userId;
    private Long shopId;
    private int rating;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReadReview fromEntity(Review review) {
        return new ReadReview(
                review.getId(),
                review.getUserId(),
                review.getShopId(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
