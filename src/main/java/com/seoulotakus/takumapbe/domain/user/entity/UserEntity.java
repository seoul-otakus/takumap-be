package com.seoulotakus.takumapbe.domain.user.entity;

import com.seoulotakus.takumapbe.domain.user.enums.Provider;
import com.seoulotakus.takumapbe.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "tbl_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nickname;
    private String password;
    private String email;
    private UserRole userRole;
    private Provider provider;   // 로그인 방식
    private String providerId;   // 소셜 로그인 고유 식별자
    private String refreshToken;
    private Boolean isActive;    // 회원 논리적 삭제 시 사용
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
