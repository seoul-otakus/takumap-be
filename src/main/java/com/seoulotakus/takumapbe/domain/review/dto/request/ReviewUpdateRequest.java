package com.seoulotakus.takumapbe.domain.review.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class ReviewUpdateRequest {
    @DecimalMin(value = "0.5", message = "평점은 0.5 이상 0.5 이하만 가능합니다")
    @DecimalMax(value = "5.0", message = "평점은 0.5 이상 0.5 이하만 가능합니다")
    private BigDecimal rating;

    @Size(max = 255, message = "내용은 255자까지 입력 가능합니다")
    private String content;

    private List<String> objectKeysToAdd = List.of();
    private List<String> objectKeysToRemove = List.of();
}
