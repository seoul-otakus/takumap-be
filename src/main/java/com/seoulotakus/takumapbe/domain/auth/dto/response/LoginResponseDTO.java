package com.seoulotakus.takumapbe.domain.auth.dto.response;

import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class LoginResponseDTO {

    private String token;
    private int expirationTime;

    public LoginResponseDTO(String token){
        this.token = token;
        this.expirationTime = 3600;
    }

}
