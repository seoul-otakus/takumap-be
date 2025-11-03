package com.seoulotakus.takumapbe.common.auth.filter;

import com.seoulotakus.takumapbe.common.auth.provider.jwt.JwtProvider;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.repository.UserRepository;
import com.seoulotakus.takumapbe.global.exception.BusinessException;
import com.seoulotakus.takumapbe.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulotakus.takumapbe.global.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT 인증 필터
     * JWT 인증 필터를 통해 인증된 사용자만 controller로 넘어가게끔,
     * 작업을 수행할 수 있게끔 하는 필터
     * **/

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

        private static final List<String> EXCLUDE_URLS = 
                List.of("/api/v1/auth/email-certification", "/api/v1/auth/id-check", "/api/v1/auth/refresh");  // 인증 불필요한 경로
    /**
     * 클라이언트에서 서버로 요청 들어오면 request 객체에서 bearer token 형태로 인증 정보를 받아온다.
     * 그럼 bearer token에서 token 값을 꺼내온 다음에 이 토큰을 JwtProvider의 validate()에 넘겨줘서
     * 이 토큰이 적절한 토큰인지 검사한 후 subject를 꺼내서 작업을 진행한다.
     * **/

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // EXCLUDE_URLS에 포함된 경로일 경우 필터링을 건너뛴다.
        if(EXCLUDE_URLS.contains(path)){
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        try {
            // token 없는 경우
            if(token != null){
                // token이 있으면 JwtProvider에 있는 validate()를 통해 검증함.
                String userId = jwtProvider.validate(token);

                // userId가 있다면(검증이 성공된다면) userId로 유저 정보 꺼내오기
                UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(() -> BusinessException.of(ErrorCode.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."));
                String userRole = userEntity.getUserRole().toString();

                // ROLE_DEVELOPER, ROLE_BOSS, ROLE_USER 형태
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(userRole));

                // SecurityContext 생성
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                // SecurityContext에 담을 token 생성 -> '이 사람이 진짜 사용자일까?'를 확인하기 위한 증명서
                AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // securityContext에 token값 담기
                securityContext.setAuthentication(authenticationToken);

                // 만든 securityContext를 request에 등록하여 실제로 사용할 수 있게 함.
                SecurityContextHolder.setContext(securityContext);
            }
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            ApiResponse<Object> apiResponse = ApiResponse.fail(HttpStatus.UNAUTHORIZED, 401, "유효하지 않은 토큰입니다.");
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            return;
        }

        filterChain.doFilter(request, response);
    }
    
    // request에서 token을 가져오는 메소드(쿠키 or 헤더)
    private String resolveToken(HttpServletRequest request){
        
        // 1. 쿠키에서 토큰 확인
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if("access_token".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        
        // 2. 쿠키에 토큰이 없으면 헤더에서 확인
        String authorization = request.getHeader("Authorization");
        if(StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")){
            return authorization.substring(7);
        }
        
        return null;
    }
}
