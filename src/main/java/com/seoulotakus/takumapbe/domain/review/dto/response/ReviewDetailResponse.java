package com.seoulotakus.takumapbe.domain.review.dto.response;

import com.seoulotakus.takumapbe.domain.review.dto.common.ImageDTO;
import com.seoulotakus.takumapbe.domain.review.dto.common.ShopDTO;
import com.seoulotakus.takumapbe.domain.review.dto.common.WriterDTO;
import com.seoulotakus.takumapbe.domain.review.entity.Review;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 단건 조회
@Getter
public class ReviewDetailResponse {
    private Long id;
    private WriterDTO writer;
    private ShopDTO shop;
    private BigDecimal rating;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ImageDTO> images;

    public ReviewDetailResponse(Review review, List<ImageDTO> images) {
        this.id = review.getId();
        this.writer = WriterDTO.of(review.getWriter().getId(), review.getWriter().getNickname());
        this.shop = ShopDTO.of(review.getShop().getId(), review.getShop().getName());
        this.rating = review.getRating();
        this.content = review.getContent();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
        this.images = images;
    }
}
