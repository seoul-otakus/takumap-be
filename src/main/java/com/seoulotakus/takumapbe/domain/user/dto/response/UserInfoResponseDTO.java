package com.seoulotakus.takumapbe.domain.user.dto.response;

import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.enums.UserRole;
import lombok.Getter;

@Getter
public class UserInfoResponseDTO {
    private final Long id;
    private final String userId;
    private final String nickname;
    private final String email;
    private final UserRole role;

    public UserInfoResponseDTO(UserEntity user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.role = user.getUserRole();
    }
}
