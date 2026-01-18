package com.seoulotakus.takumapbe.domain.shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
public class ShopCreateRequest {
    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;

    @NotBlank(message = "상점 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    private String description;
    private String phoneNumber;

    @NotNull
    private LocalTime openTime;
    @NotNull
    private LocalTime closeTime;

    @NotNull
    private BigDecimal latitude;
    @NotNull
    private BigDecimal longitude;
}