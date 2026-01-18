package com.seoulotakus.takumapbe.domain.user.dto.response;

import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.enums.Provider;
import com.seoulotakus.takumapbe.domain.user.enums.UserRole;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {
    private Long id;
    private String nickname;
    private String userId;
    private String email;
    private UserRole role;
    private Provider provider;
    private Boolean isActive;
    private LocalDateTime createdAt;

    /**
     * UserEntity를 UserListResponse로 변환하는 정적 팩토리 메서드
     * 비밀번호와 refreshToken은 제외
     */
    public static UserListResponse from(UserEntity user) {
        return UserListResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getUserRole())
                .provider(user.getProvider())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
