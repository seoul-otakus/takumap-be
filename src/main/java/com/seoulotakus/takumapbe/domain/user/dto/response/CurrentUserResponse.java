package com.seoulotakus.takumapbe.domain.user.dto.response;

import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.enums.UserRole;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserResponse {
    private Long id;
    private String nickname;
    private String userId;
    private String email;
    private UserRole role;

    /**
     * UserEntity를 CurrentUserResponse로 변환하는 정적 팩토리 메서드
     * 현재 로그인한 유저의 정보 반환 (role 포함)
     */
    public static CurrentUserResponse from(UserEntity user) {
        return CurrentUserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getUserRole())
                .build();
    }
}
