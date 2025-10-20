package com.seoulotakus.takumapbe.domain.review.dto.request;

import lombok.Getter;

@Getter
public class UpdateReview {
    private int rating;
    private String content;
}
