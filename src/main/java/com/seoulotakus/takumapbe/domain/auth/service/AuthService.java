package com.seoulotakus.takumapbe.domain.auth.service;


import com.seoulotakus.takumapbe.domain.auth.dto.request.*;
import com.seoulotakus.takumapbe.domain.auth.dto.response.LoginResponseDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    void idCheck(IdCheckRequestDTO requestDTO);
    void certificateEmail(EmailCertificationRequestDTO emailRequestDTO);
    void checkCertification(CheckCertificationRequestDTO certificationRequestDTO);
    void signUp(SignUpRequestDTO signUpRequestDTO);
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
