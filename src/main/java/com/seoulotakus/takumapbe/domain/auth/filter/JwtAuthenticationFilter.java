package com.seoulotakus.takumapbe.domain.auth.filter;

import com.seoulotakus.takumapbe.domain.auth.jwt.JwtProvider;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.repository.UserRepository;
import com.seoulotakus.takumapbe.global.exception.BusinessException;
import com.seoulotakus.takumapbe.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 클라이언트에서 서버로 요청 들어오면 request 객체에서 bearer token 형태로 인증 정보를 받아온다.
     * 그럼 bearer token에서 token 값을 꺼내온 다음에 이 토큰을 JwtProvider의 validate()에 넘겨줘서
     * 이 토큰이 적절한 토큰인지 검사한 후 subject를 꺼내서 작업을 진행한다.
     * **/

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            String token = parseBearerToken(request);

            // authorization이 없거나 Bearer 방식이 아닌 경우 doFilter()로 작업 넘기기
            if(token == null){
                filterChain.doFilter(request, response);
                return;
            }

            // token이 있으면 JwtProvider에 있는 validate()를 통해 검증함.
            String userId = jwtProvider.validate(token);
            if(userId == null){
                filterChain.doFilter(request, response);
                return;
            }

            // userId가 있다면(검증이 성공된다면) userId로 유저 정보 꺼내오기
            UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(() -> BusinessException.of(ErrorCode.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."));
            String userRole = userEntity.getUserRole().toString();

            // ROLE_DEVELOPER, ROLE_BOSS, ROLE_USER 형태
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(userRole));

            // SecurityContext 생성
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

            // SecurityContext에 담을 token 생성
            AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // securityContext에 token값 담기
            securityContext.setAuthentication(authenticationToken);

            // 만든 securityContext를 request에 등록하여 실제로 사용할 수 있게 함.
            SecurityContextHolder.setContext(securityContext);

        } catch (Exception exception){
            exception.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    // bearer token에서 token을 꺼내오는 작업
    private String parseBearerToken(HttpServletRequest request){

        /* request로부터 header에 있는 authorization값을 가져오기 */
        String authorization = request.getHeader("Authorization");

        boolean hasAuthorization = StringUtils.hasText(authorization); // 실제 값이 존재하는지 확인하는 메소드
        if(!hasAuthorization){
            return null;
        }

        boolean isBearer = authorization.startsWith("Bearer "); // authorization이 bearer 인증 방식인지 확인
        if(!isBearer){
            return null;
        }

        String token = authorization.substring(7); // "Bearer " 이후의 값 추출 후 반환
        return token;
    }
}
