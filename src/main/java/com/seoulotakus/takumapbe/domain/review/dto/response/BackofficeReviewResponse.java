package com.seoulotakus.takumapbe.domain.review.dto.response;

import com.seoulotakus.takumapbe.domain.review.dto.common.ImageDTO;
import com.seoulotakus.takumapbe.domain.review.entity.Review;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BackofficeReviewResponse {
    private Long reviewId;
    private Long shopId;
    private String shopName;
    private Long writerId;
    private String writerNickname;
    private BigDecimal rating;
    private String content;
    private List<ImageDTO> images;
    private LocalDateTime createdAt;

    public BackofficeReviewResponse(Review review, List<ImageDTO> images) {
        this.reviewId = review.getId();
        this.shopId = review.getShop().getId();
        this.shopName = review.getShop().getName();
        this.writerId = review.getWriter().getId();
        this.writerNickname = review.getWriter().getNickname();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.images = images;
        this.createdAt = review.getCreatedAt();
    }
}