package com.seoulotakus.takumapbe.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailCertificationRequestDTO {

    @NotBlank(message = "userId는 필수입니다.")
    private String userId;

    @Email
    @NotBlank(message = "email은 필수입니다.")
    private String email;
}
