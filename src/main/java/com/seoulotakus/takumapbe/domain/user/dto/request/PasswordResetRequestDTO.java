package com.seoulotakus.takumapbe.domain.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequestDTO {

    private String userId;
    private String email;

    public PasswordResetRequestDTO(){}

    public PasswordResetRequestDTO(String userId, String email){
        this.userId = userId;
        this.email = email;
    }
}
