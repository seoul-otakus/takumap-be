package com.seoulotakus.takumapbe.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@MappedSuperclass  // 자식 엔티티가 이 필드를 테이블 컬럼으로 자동 매핑할 수 있음
@Getter
@Setter
public abstract class CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_at", updatable = false)
    private Timestamp createAt;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "updated_at")
    protected Timestamp updatedAt;
    @Column(name = "updated_by")
    protected Long updatedBy;

}
