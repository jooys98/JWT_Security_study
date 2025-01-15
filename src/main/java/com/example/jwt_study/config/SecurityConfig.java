package com.example.jwt_study.config;

import com.example.jwt_study.component.CustomAccessDeniedHandler;
import com.example.jwt_study.component.CustomAuthenticationEntryPoint;
import com.example.jwt_study.security.jwt.JwtFilter;
import com.example.jwt_study.security.jwt.JwtUtil;
import com.example.jwt_study.security.jwt.LoginFilter;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  /*
  * 일반적인 config 패키지에서 다루는 설정들:

보안 설정 (SecurityConfig)
데이터베이스 설정 (DatabaseConfig)
웹 설정 (WebConfig)
스웨거 설정 (SwaggerConfig)
Redis 설정 (RedisConfig)*/

    private final AuthenticationConfiguration authenticationConfiguration;
    // 스프링 시큐리티의 인증 관련 설정을 담당하는 객체
    // AuthenticationManager를 생성하는데 사용됨
    private final JwtUtil jwtUtil;
    // JWT 토큰의 생성, 검증, 정보 추출 하는 클래스
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    //인증이 필요한 경로에 접근했을떄 막아주는 클래스
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    //인가가 필요한 경로에 접근했을때 막아주는 클래스

    @Bean //비밀번호 암호화를 위한 인코더
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean //인증관리자
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean // 로그인 성공시 처리할 핸들러
    public LogoutSuccessHandler logoutHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write("logout success");
        };
    }

    @Bean //스프링 시큐리티의 주요 보안 설정 구성
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())

                //페이지별 접근 권한 설정
                .authorizeHttpRequests(authorize -> // 인증 없이도 접근 가능한 경로들
                        authorize.requestMatchers("/", "/api/users/join", "/api/users/id", "/api/users/check",
                                        "/login", "/logout", "/api/posts/**", "/api/posts/search",
                                        "/api/posts/search/tag", "/api/posts/all", "/api/posts/all2", "/api/posts/{id}",
                                        "/api/main-categories", "/api/sub-categories")
                                .permitAll()
                                //user 권한이 필요한 경로
                                .requestMatchers("/api/posts").hasAnyRole("USER")
                                //그외 모든 요청은 인증이 필요함
                                .anyRequest().authenticated()
                );

        // CORS 설정
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true); // 인증 정보 포함 허용
            //접근 허용할 도메인 설정
            config.setAllowedOrigins(
                    Arrays.asList("http://localhost:3000", "http://localhost:3001",
                            "http://localhost:3002", "http://localhost")
            );
            config.addAllowedHeader("*"); // 모든 헤더 허용
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            //허용할 메서드
            config.addExposedHeader("Authorization");
            //인증정보 헤더 노출
            return config;
        }));

        //로그아웃 설정
        http.logout(logout ->
                logout.logoutUrl("/logout")
                        .logoutSuccessHandler(logoutHandler())
                        .addLogoutHandler((request, response, authentication) -> {
                            if (request.getSession() != null) {
                                request.getSession().invalidate();
                            }
                        })
                        .deleteCookies("JSESSIONID", "Authorization", "access", "refresh", "token")
        );
//대충 세션 방식 안쓴다는 뜻
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //호출할 jwtUtil 추가 ( 토큰을 만들어줌)
        http.addFilterBefore(new JwtFilter(this.jwtUtil), LoginFilter.class);
        //로그인 필터 추가
        //UsernamePasswordAuthenticationFilter 는 기본적으로 /login ,post 요청을 처리함
        //이를 상속받은 loginfilter 클래스 생성자 호출하여 로그인 처리

        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
                UsernamePasswordAuthenticationFilter.class);

        //인증/인가 , 예외처리 설정
        http.exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(customAuthenticationEntryPoint);
                    exception.accessDeniedHandler(customAccessDeniedHandler);
                }
        );

        return http.build();
    }

}