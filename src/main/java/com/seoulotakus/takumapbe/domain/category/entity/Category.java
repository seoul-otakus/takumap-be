package com.seoulotakus.takumapbe.domain.category.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "tbl_category", schema = "takumap_db")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 20)
    @Column(name = "name", length = 20)
    private String name;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Builder
    public Category(String name, LocalDate createdAt, Long createdBy) {
        this.name = name;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }
}