package com.seoulotakus.takumapbe.domain.auth.dto.request;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckCertificationRequestDTO {

    @NotBlank
    private String userId;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String certificationNumber;
}
