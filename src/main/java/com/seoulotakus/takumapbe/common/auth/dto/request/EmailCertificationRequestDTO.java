package com.seoulotakus.takumapbe.common.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailCertificationRequestDTO {

    @NotNull(message = "userId는 필수입니다.")
    private String userId;

    @Email
    @NotNull(message = "email은 필수입니다.")
    private String email;
}
