package com.seoulotakus.takumapbe.common.oauth.handler;

import com.seoulotakus.takumapbe.common.auth.provider.jwt.JwtProvider;
import com.seoulotakus.takumapbe.common.config.PrincipalDetails;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendUrl;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // PrincipalDetails에서 UserEntity 가져오기
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        UserEntity user = principalDetails.getUser();
        String userId = user.getUserId();

        // JWT 토큰 생성
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        // Refresh Token DB 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // 프로덕션 환경 여부 확인
        boolean isProduction = "prod".equals(activeProfile);

        // ResponseCookie를 사용하여 SameSite=None 설정 (크로스 도메인 쿠키 지원)
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", accessToken)
                .path("/")
                .maxAge(60 * 60)  // 1시간
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")  // 크로스 도메인에서는 None 필요
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)  // 7일
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")
                .build();

        // 응답에 쿠키 추가
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // 프론트엔드 메인 페이지로 리다이렉트
        response.sendRedirect(frontendUrl);
    }
}
