package com.seoulotakus.takumapbe.domain.review.dto.request;

import lombok.Getter;

@Getter
public class CreateReview {
    private Long userId;
    private Long shopId;
    private int rating;
    private String content;
}
