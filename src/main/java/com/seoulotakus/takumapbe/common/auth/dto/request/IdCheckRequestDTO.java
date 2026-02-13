package com.seoulotakus.takumapbe.common.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IdCheckRequestDTO {

    @NotNull(message = "아이디는 필수 입력값입니다.")
    private String userId;
}
