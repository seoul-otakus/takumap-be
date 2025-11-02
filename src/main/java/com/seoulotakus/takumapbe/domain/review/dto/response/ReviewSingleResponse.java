package com.seoulotakus.takumapbe.domain.review.dto.response;

import com.seoulotakus.takumapbe.domain.review.dto.common.ImageDTO;
import com.seoulotakus.takumapbe.domain.review.dto.common.WriterDTO;
import com.seoulotakus.takumapbe.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 목록 조회 (개별)
@Getter
@AllArgsConstructor
public class ReviewSingleResponse {
    private Long id;
    private WriterDTO writer;
    private Long shopId;
    private BigDecimal rating;
    private String content;
    private LocalDateTime createdAt;
    private List<ImageDTO> images;

    public ReviewSingleResponse(Review review, List<ImageDTO> images) {
        this.id = review.getId();
        this.writer = WriterDTO.of(review.getWriter().getId(), review.getWriter().getNickname());
        this.shopId = review.getShop().getId();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.createdAt = review.getCreatedAt();
        this.images = images;
    }
}
