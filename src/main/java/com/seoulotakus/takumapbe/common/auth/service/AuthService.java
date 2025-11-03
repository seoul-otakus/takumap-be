package com.seoulotakus.takumapbe.common.auth.service;


import com.seoulotakus.takumapbe.common.auth.dto.request.*;
import com.seoulotakus.takumapbe.common.auth.dto.response.AccessTokenResponseDTO;
import com.seoulotakus.takumapbe.common.auth.dto.response.LoginResponseDTO;

public interface AuthService {

    void idCheck(IdCheckRequestDTO requestDTO);
    void certificateEmail(EmailCertificationRequestDTO emailRequestDTO);
    void checkCertification(CheckCertificationRequestDTO certificationRequestDTO);
    void signUp(SignUpRequestDTO signUpRequestDTO);
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
    AccessTokenResponseDTO refreshAccessToken(String refreshToken);
    void logout(String refreshToken);
}
