package com.seoulotakus.takumapbe.global.config;

import com.seoulotakus.takumapbe.domain.auth.filter.JwtAuthenticationFilter;
import com.seoulotakus.takumapbe.global.config.oauth.PrincipalOauth2UserService;
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
    private PrincipalOauth2UserService principalOauth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /* swagger에 대한 요청 제외 */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){

        return (web) -> web.ignoring()
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
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
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )  // session 사용하지 않음
            .authorizeHttpRequests(auth -> auth
                // 권한이 없을 때도 들어갈 수 있는 경로들에 대한 접근 권한 설정
                .requestMatchers( "/", "/main", "/api/vi/auth/**").permitAll()
    //            .requestMatchers("/api/v1/auth/oauth/me").authenticated()
                // 유저일 때만 들어갈 수 있는 권한 설정
                .requestMatchers("/api/v1/user/**").hasRole("USER")
                // 관리자일 때만 들어갈 수 있는 권한 설정
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // 인증 실패 시
            .exceptionHandling(exceptionHandling -> exceptionHandling
                    .authenticationEntryPoint(new FailedAuthenticationEntryPoint())
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // 로그인 시 설정
            .formLogin(login -> login
            // 로그인 페이지를 찾아주는 메소드
            .loginPage("/auth/login")
            .loginProcessingUrl("/auth/login")
            // 사용자 id 입력 필드와 사용자 Pass 입력 필드가 일치해야 들어갈 수 있다.
            .usernameParameter("userId")
            .passwordParameter("password")
            .defaultSuccessUrl("/", true)
//            .successHandler(custumLoginSuccessHandler)
            // 실패 시 처리할 핸들러 등록
//            .failureHandler(authFailHandler)
            .permitAll()

            // 로그아웃 시 설정
        ).logout(logout -> logout
            // 로그아웃 요청 들어올 때
            .logoutUrl("/auth/logout")
            // 세션 삭제
            .deleteCookies("JSESSIONID")
            //
            .invalidateHttpSession(true)
            // 로그아웃 성공 시 URL을 main으로 보냄
            .logoutSuccessUrl("/")
        ).oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(principalOauth2UserService))
            .defaultSuccessUrl("/", true)
        );

        return http.build();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource(){

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // 모든 출처에 대해서 허용
        configuration.addAllowedMethod("*"); // 모든 메소드에 대해서 허용
        configuration.addAllowedHeader("*"); // 모든 헤더에 대해서 허용

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