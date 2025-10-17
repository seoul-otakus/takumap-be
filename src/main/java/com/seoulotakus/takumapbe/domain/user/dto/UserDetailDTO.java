package com.seoulotakus.takumapbe.domain.user.dto;

import com.seoulotakus.takumapbe.domain.user.enums.Provider;
import com.seoulotakus.takumapbe.domain.user.enums.UserRole;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {

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
