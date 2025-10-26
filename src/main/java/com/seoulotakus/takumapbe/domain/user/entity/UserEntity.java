package com.seoulotakus.takumapbe.domain.user.entity;

import com.seoulotakus.takumapbe.domain.user.enums.Provider;
import com.seoulotakus.takumapbe.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "tbl_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole userRole;
    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private Provider provider;   // 로그인 방식
    @Column(name = "provider_id")
    private String providerId;   // 소셜 로그인 고유 식별자
    @Column(name = "refresh_token")
    private String refreshToken;
    @Column(name = "is_active")
    private Boolean isActive;    // 회원 논리적 삭제 시 사용
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "created_by")
    private long createdBy;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "updated_by")
    private long updatedBy;

    public UserEntity update(String nickname, String email){
        this.nickname = nickname;
        this.email = email;
        this.updatedAt = new Timestamp(System.currentTimeMillis());

        return this;
    }

    public void setUserId(String providerNikname) {
        this.nickname = providerNikname;
    }

    public void setPassword(String providerPassword){
        this.password = providerPassword;
    }
}
