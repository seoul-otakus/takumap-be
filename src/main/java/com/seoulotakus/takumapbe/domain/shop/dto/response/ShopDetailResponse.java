package com.seoulotakus.takumapbe.domain.shop.dto.response;

import com.seoulotakus.takumapbe.domain.shop.entity.Shop;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
public class ShopDetailResponse {
    private Long id;
    private String categoryName;
    private String name;
    private String address;
    private String description;
    private String phoneNumber;
    private LocalTime openTime;
    private LocalTime closeTime;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String writerName; // 등록자 닉네임 등

    public ShopDetailResponse(Shop shop) {
        this.id = shop.getId();
        this.categoryName = shop.getCategory().getName();
        this.name = shop.getName();
        this.address = shop.getAddress();
        this.description = shop.getDescription();
        this.phoneNumber = shop.getPhoneNumber();
        this.openTime = shop.getOpenTime();
        this.closeTime = shop.getCloseTime();
        this.latitude = shop.getLatitude();
        this.longitude = shop.getLongitude();
        // createdBy가 null일 수 있는 상황을 대비하거나 로직에 맞게 처리
        this.writerName = shop.getCreatedBy() != null ? shop.getCreatedBy().getNickname() : null;
    }
}