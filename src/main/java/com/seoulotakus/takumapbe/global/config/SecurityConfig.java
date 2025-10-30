package com.seoulotakus.takumapbe.global.config;

import com.seoulotakus.takumapbe.global.config.oauth.PrincipalOauth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity  // 스프링 시큐리티 필터(SecurityConfig)가 스프링 필터체인(기본 필터체인)에 등록이 된다.
public class SecurityConfig {

//    @Autowired
//    private AuthFailHandler authFailHandler;

//    @Autowired
//    private CustumLoginSuccessHandler custumLoginSuccessHandler;

    @Lazy
    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

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
    public SecurityFilterChain configure(HttpSecurity http) throws Exception{
        /* 요청에 대한 권한 체크 */
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            // 권한이 없을 때도 들어갈 수 있는 경로들에 대한 접근 권한 설정
            .requestMatchers("/auth/login", "/auth/fail", "/", "/main").permitAll()
            .requestMatchers("/api/auth/oauth/me").authenticated()
            // 유저일 때만 들어갈 수 있는 권한 설정
            .requestMatchers("/user/*").hasRole("USER")
            // 관리자일 때만 들어갈 수 있는 권한 설정
            .requestMatchers("/admin/*").hasRole("ADMIN")
//            .anyRequest().authenticatedz()
            .anyRequest().permitAll()

            // 로그인 시 설정
        ).formLogin(login -> login
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
}