package com.seoulotakus.takumapbe.domain.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetConfirmRequestDTO {

    private String userId;
    private String email;
    private String oneTimePassword;
    private String newPassword;

    public PasswordResetConfirmRequestDTO(){}

    public PasswordResetConfirmRequestDTO(String userId, String email, String oneTimePassword, String newPassword){
        this.userId = userId;
        this.email = email;
        this.oneTimePassword = oneTimePassword;
        this.newPassword = newPassword;
    }

}
