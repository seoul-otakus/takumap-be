package com.seoulotakus.takumapbe.domain.review.entity;

import com.seoulotakus.takumapbe.domain.shop.entity.Shop;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_reviews")
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "rating", nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "content")
    private String content;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private UserEntity updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // == 생성자 ==
    @Builder
    public Review(UserEntity writer, Shop shop, BigDecimal rating, String content) {
        this.writer = writer;
        this.shop = shop;
        this.rating = rating;
        this.content = content;
    }

    // == 수정용 메소드 ==
    public void update(BigDecimal rating, String content, UserEntity updatedBy) {
        this.rating = rating;
        this.content = content;
        this.updatedBy = updatedBy;
    }

    // == 삭제용 메소드 ==
    public void softDelete(UserEntity user) {
        this.deletedAt = LocalDateTime.now();
        this.updatedBy = user;
    }
}
