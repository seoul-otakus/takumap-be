package com.seoulotakus.takumapbe.common.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "tbl_certification")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "email")
    private String email;
    @Column(name = "certification_number")
    private String certificationNumber;
    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
}
