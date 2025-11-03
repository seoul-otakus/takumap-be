package com.seoulotakus.takumapbe.common.auth.dto.response;

import com.seoulotakus.takumapbe.domain.user.enums.UserRole;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class LoginResponseDTO {

    private String nickname;
    private UserRole role;
    private Boolean isActive;
    private String token;
    private String refreshToken;
    private int expirationTime;

    public LoginResponseDTO(String nickname, UserRole role, Boolean isActive, String token, String refreshToken){
        this.nickname = nickname;
        this.role = role;
        this.isActive = isActive;
        this.token = token;
        this.refreshToken = refreshToken;
        this.expirationTime = 3600;
    }

}
