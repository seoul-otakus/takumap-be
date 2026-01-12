package com.seoulotakus.takumapbe.global.config;

import com.seoulotakus.takumapbe.common.auth.filter.JwtAuthenticationFilter;
import com.seoulotakus.takumapbe.common.oauth.handler.OAuth2SuccessHandler;
import com.seoulotakus.takumapbe.common.oauth.service.PrincipalOAuth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;

@Slf4j
@Configurable  // @Bean 어노테이션을 등록할 수 있게 해줌
@Configuration  // SecurityConfig 클래스가 Bean이라는 메소드를 가지고 있는 클래스임을 나타냄
@EnableWebSecurity  // 스프링 시큐리티 필터(SecurityConfig)가 스프링 필터체인(기본 필터체인)에 등록이 된다.
@RequiredArgsConstructor
public class SecurityConfig {

//    @Autowired
//    private AuthFailHandler authFailHandler;

//    @Autowired
//    private CustumLoginSuccessHandler custumLoginSuccessHandler;

    @Autowired
    private PrincipalOAuth2UserService principalOAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @org.springframework.beans.factory.annotation.Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendUrl;

    /* swagger에 대한 요청 제외 */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){

        return (web) -> web.ignoring()
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui/index.html/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                )
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception{
        /* 요청에 대한 권한 체크 */
        http
            .cors(cors -> cors
                .configurationSource(corsConfigurationSource())
            )
            .csrf(CsrfConfigurer::disable)
            .httpBasic(HttpBasicConfigurer::disable)  // Basic 인증 방식 말고 Bearer 인증 방식 사용.
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // session 사용하지 않음
            )
            .authorizeHttpRequests(auth -> auth
                // 권한이 없을 때도 들어갈 수 있는 경로들에 대한 접근 권한 설정
                .requestMatchers( "/",
                        "/main",
                        "/api/v1/auth/id-check",
                        "/api/v1/auth/nickname-check",
                        "/api/v1/auth/email-certification",
                        "/api/v1/auth/check-certification",
                        "/api/v1/auth/sign-up",
                        "/api/v1/auth/sign-in",
                        "/api/v1/auth/logout",
                        "/api/v1/auth/refresh",
                        "/api/v1/oauth2/**",
                        "/api/v1/favicon.ico",
                        "/api/v1/reviews",
                        "/api/v1/shops",
                        "/api/v1/shops/**"
                ).permitAll()
                .requestMatchers("/api/v1/auth/check").authenticated()
                // 유저일 때만 들어갈 수 있는 권한 설정
                .requestMatchers("/api/v1/users/**").hasRole("USER")
                // 관리자일 때만 들어갈 수 있는 권한 설정
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // 인증 실패 시
            .exceptionHandling(exceptionHandling -> exceptionHandling
                    .authenticationEntryPoint(new FailedAuthenticationEntryPoint())
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(endpoint -> endpoint.baseUri("/api/v1/oauth2"))
                .redirectionEndpoint(endpoint -> endpoint.baseUri("/api/v1/oauth2/callback/*"))
                .userInfoEndpoint(userInfo -> userInfo.userService(principalOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
            );

        return http.build();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource(){

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(frontendUrl); // 환경변수에서 프론트엔드 URL 가져오기
        configuration.addAllowedOrigin("http://localhost:3000"); // 로컬 개발용
        configuration.addAllowedMethod("*"); // 모든 메소드에 대해서 허용
        configuration.addAllowedHeader("*"); // 모든 헤더에 대해서 허용
        configuration.setAllowCredentials(true);

        // 💡 필수 수정: 서버가 클라이언트에게 Set-Cookie 헤더를 노출하도록 허용합니다.
        // Set-Cookie 헤더가 없으면 브라우저는 HTTP-Only 쿠키를 저장할 수 없습니다.
        configuration.addExposedHeader("Set-Cookie");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

// 인가 실패 시
class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint{

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // {"code" : "NP", "message" : "No Permission"}
        response.getWriter().write("{\"code\" : \"NP\", \"message\" : \"No Permission\"}");
    }
}