package com.seoulotakus.takumapbe.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    @Autowired
//    private AuthFailHandler authFailHandler;

//    @Autowired
//    private CustumLoginSuccessHandler custumLoginSuccessHandler;

    /* 비밀번호 암호화 */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

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
        http.authorizeHttpRequests(auth -> auth
            // 유저일 때만 들어갈 수 있는 권한 설정
            .requestMatchers("/user/*").hasRole("USER")
            // 권한이 없을 때도 들어갈 수 있는 경로들에 대한 접근 권한 설정
            .requestMatchers("/auth/login", "/auth/fail", "/", "/main").permitAll()
            // 관리자일 때만 들어갈 수 있는 권한 설정
            .requestMatchers("/admin/*").hasRole("ADMIN")
            .anyRequest().authenticated()

            // 로그인 시 설정
        ).formLogin(login -> login
            // 로그인 페이지를 찾아주는 메소드
            .loginPage("/auth/login")
            .loginProcessingUrl("/auth/login")
            // 사용자 id 입력 필드와 사용자 Pass 입력 필드가 일치해야 들어갈 수 있다.
            .usernameParameter("userId")
            .passwordParameter("password")
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
        ).sessionManagement(session -> {
            // 세션을 최대 2개만 허용
            session.maximumSessions(2);
            // 세션이 만료되었을 때 로그인 페이지로 보냄
            session.invalidSessionUrl("/auth/login");

            // 악의적인 사용자가 들어와 막혔을 때
        }). csrf(csrf -> csrf.disable());

        return http.build();
    }
}