package com.seoulotakus.takumapbe.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Column(name = "created_at")
    private Timestamp createdAt;
}
