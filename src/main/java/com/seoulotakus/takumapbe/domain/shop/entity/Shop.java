package com.seoulotakus.takumapbe.domain.shop.entity;

import com.seoulotakus.takumapbe.domain.category.entity.Category;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tbl_shop")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_category", nullable = false)
    private Category category;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "description")
    private String description;

    @Column(name = "phone_number", length = 11)
    private String phoneNumber;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private UserEntity updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Soft Delete용 필드 추가

    // == 생성자 (Builder) ==
    @Builder
    public Shop(Category category, String name, String address, String description,
                String phoneNumber, LocalTime openTime, LocalTime closeTime,
                BigDecimal latitude, BigDecimal longitude, UserEntity createdBy) {
        this.category = category;
        this.name = name;
        this.address = address;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdBy = createdBy;
    }

    // == 수정용 메소드 ==
    public void update(Category category, String name, String address, String description,
                       String phoneNumber, LocalTime openTime, LocalTime closeTime,
                       BigDecimal latitude, BigDecimal longitude, UserEntity updatedBy) {
        this.category = category;
        this.name = name;
        this.address = address;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedBy = updatedBy;
    }

    // == 삭제용 메소드 (Soft Delete) ==
    public void softDelete(UserEntity user) {
        this.deletedAt = LocalDateTime.now();
        this.updatedBy = user;
    }
}