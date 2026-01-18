package com.seoulotakus.takumapbe.common.auth.controller;

import com.seoulotakus.takumapbe.common.auth.dto.request.*;
import com.seoulotakus.takumapbe.common.auth.dto.response.AccessTokenResponseDTO;
import com.seoulotakus.takumapbe.common.auth.dto.response.LoginResponseDTO;
import com.seoulotakus.takumapbe.common.auth.service.AuthService;
import com.seoulotakus.takumapbe.global.response.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /** id 중복 체크 **/
    @PostMapping("/id-check")
    public ResponseEntity<ApiResponse<?>> idCheck(@RequestBody @Valid IdCheckRequestDTO requestDTO){
        authService.idCheck(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(null, "사용 가능한 아이디입니다."));
    }

    /** 닉네입 중복 체크 **/
    @PostMapping("/nickname-check")
    public ResponseEntity<ApiResponse<?>> nicknameCheck(@RequestBody @Valid NicknameCheckRequestDTO requestDTO){
        authService.nicknameCheck(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(null, "사용 가능한 닉네임입니다."));
    }

    /** 이메일 인증 번호 발송 **/
    @PostMapping("/email-certification")
    public ResponseEntity<ApiResponse<?>> emailCertification(
            @RequestBody @Valid EmailCertificationRequestDTO requestDTO) {
        authService.certificateEmail(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(null, "인증 이메일 발송에 성공했습니다."));
    }

    /** 이메일 인증 번호 확인 **/
    @PostMapping("/check-certification")
    public ResponseEntity<ApiResponse<?>> checkCertification(@RequestBody @Valid CheckCertificationRequestDTO requestDTO){
        authService.checkCertification(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(null, "이메일 인증에 성공했습니다."));
    }

    /** 자체 회원가입 **/
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<?>> signUp(@Valid @RequestBody SignUpRequestDTO requestDTO){
        authService.signUp(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(null, "회원가입에 성공했습니다."));
    }

    /** 자체 로그인 **/
    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequestDTO requestDTO, HttpServletResponse response){

        LoginResponseDTO loginResponseDTO = authService.login(requestDTO);
        boolean isProduction = "prod".equals(activeProfile);

        // Access Token을 쿠키에 설정
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", loginResponseDTO.getToken())
                .path("/")
                .maxAge(60 * 60)  // 1시간
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        // Refresh Token을 쿠키에 설정
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", loginResponseDTO.getRefreshToken())
                .path("/")
                .maxAge(7 * 24 * 60 * 60)  // 7일
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(ApiResponse.success(loginResponseDTO, "로그인에 성공했습니다."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshAccessToken(@CookieValue("refresh_token") String refreshToken, HttpServletResponse response){
        AccessTokenResponseDTO responseDTO = authService.refreshAccessToken(refreshToken);
        boolean isProduction = "prod".equals(activeProfile);

        // 새로운 Access Token을 쿠키에 설정
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", responseDTO.getAccessToken())
                .path("/")
                .maxAge(60 * 60)  // 1시간
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        return ResponseEntity.ok(ApiResponse.success(responseDTO, "AccessToken이 재발급되었습니다."));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletResponse response, @CookieValue("refresh_token") String refreshToken){
        authService.logout(refreshToken);
        boolean isProduction = "prod".equals(activeProfile);

        // access_token 쿠키 삭제
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", "")
                .path("/")
                .maxAge(0)
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        // refresh_token 쿠키 삭제
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "")
                .path("/")
                .maxAge(0)
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // JSESSIONID 쿠키 삭제
        ResponseCookie jsessionidCookie = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .maxAge(0)
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jsessionidCookie.toString());

        return ResponseEntity.ok(ApiResponse.success("로그아웃에 성공했습니다."));
    }
}
