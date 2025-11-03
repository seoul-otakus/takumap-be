package com.seoulotakus.takumapbe.common.oauth.handler;

import com.seoulotakus.takumapbe.common.auth.provider.jwt.JwtProvider;
import com.seoulotakus.takumapbe.common.config.PrincipalDetails;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

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

        // 쿠키 생성 및 설정
        Cookie accessTokenCookie = new Cookie("access_token", accessToken);
        accessTokenCookie.setPath("/"); // 쿠키가 전송될 경로
        accessTokenCookie.setMaxAge(60 * 60);  // 쿠키 유효 시간(1시간)
//        accessTokenCookie.setSecure(true);  // HTTPS에서만 쿠키 전송(배포시 활성화)
        accessTokenCookie.setHttpOnly(true);  // JavaScript에서 쿠키 접근 불가

        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);  // 7일
//        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setHttpOnly(true);

        // 응답에 쿠키 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        // 프론트엔드 메인 페이지로 리다이렉트
        response.sendRedirect("http://localhost:3000");
    }
}
