package com.seoulotakus.takumapbe.common.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NicknameCheckRequestDTO {

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;

}
