package com.seoulotakus.takumapbe.domain.shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class ShopUpdateRequest {

    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;

    @NotBlank(message = "상점 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    private String description;

    private String phoneNumber;

    @NotNull(message = "오픈 시간은 필수입니다.")
    private LocalTime openTime;

    @NotNull(message = "마감 시간은 필수입니다.")
    private LocalTime closeTime;

    @NotNull(message = "위도는 필수입니다.")
    private BigDecimal latitude;

    @NotNull(message = "경도는 필수입니다.")
    private BigDecimal longitude;
}