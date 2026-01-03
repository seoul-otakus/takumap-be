package com.seoulotakus.takumapbe.common.auth.controller;

import com.seoulotakus.takumapbe.common.auth.dto.request.*;
import com.seoulotakus.takumapbe.common.auth.dto.response.AccessTokenResponseDTO;
import com.seoulotakus.takumapbe.common.auth.dto.response.LoginResponseDTO;
import com.seoulotakus.takumapbe.common.auth.service.AuthService;
import com.seoulotakus.takumapbe.global.response.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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

        // Access Token을 쿠키에 설정
        Cookie accessTokenCookie = new Cookie("access_token", loginResponseDTO.getToken());
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(30); // 30초
        accessTokenCookie.setHttpOnly(true);
        response.addCookie(accessTokenCookie);

        // Refresh Token을 쿠키에 설정
        Cookie refreshTokenCookie = new Cookie("refresh_token", loginResponseDTO.getRefreshToken());
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(604800); // 7일
        refreshTokenCookie.setHttpOnly(true);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(ApiResponse.success(loginResponseDTO, "로그인에 성공했습니다."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshAccessToken(@CookieValue("refresh_token") String refreshToken, HttpServletResponse response){
        AccessTokenResponseDTO responseDTO = authService.refreshAccessToken(refreshToken);

        // 새로운 Access Token을 쿠키에 설정
        Cookie accessTokenCookie = new Cookie("access_token", responseDTO.getAccessToken());
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60); // 1시간
        accessTokenCookie.setHttpOnly(true);
        // accessTokenCookie.setSecure(true); // 배포 시 활성화
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok(ApiResponse.success(responseDTO, "AccessToken이 재발급되었습니다."));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletResponse response, @CookieValue("refresh_token") String refreshToken){
        authService.logout(refreshToken);

        // access_token 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("access_token", null);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        response.addCookie(accessTokenCookie);

        // refresh_token 쿠키 삭제
        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);

        // JSESSIONID 쿠키 삭제
        Cookie jsessionidCookie = new Cookie("JSESSIONID", null);
        jsessionidCookie.setPath("/");
        jsessionidCookie.setMaxAge(0);
        response.addCookie(jsessionidCookie);

        return ResponseEntity.ok(ApiResponse.success("로그아웃에 성공했습니다."));
    }
}
