package com.example.soccer.config;

import com.example.soccer.security.*;
import jakarta.servlet.http.HttpServletRequest;
import com.example.soccer.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
//    private final DefaultOAuth2UserService oAuth2UserService;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomUserDetailsService customUserDetailsService;
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public static final String[] allowUrls = {
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/api/v1/posts/**",
            "/api/v1/replies/**",
            "/login",
            "/api/auth/login/kakao/**",
            "/oauth2/**",
            "/",
            "/auth/login/kakao/**",
            "/auth/failure",
            "/auth/success"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, customUserDetailsService);
        http
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
//                        config.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://127.0.0.1:5500")); > 안된다
                        config.setAllowedOrigins(Collections.singletonList("http://127.0.0.1:5500"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L); //1시간
                        return config;
                    }
                }))
//                .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 로그인 비활성화
//                .headers(c -> c.frameOptions(
//                        FrameOptionsConfig::disable).disable()) // X-Frame-Options 비활성화
                .authorizeHttpRequests(auth -> auth // 인증 초점
                        // 누구나 접근 가능한 API
                        .requestMatchers(HttpMethod.POST, "/api/members").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members/signcheck").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/members/{memberId}").permitAll() // ${}은 안먹힘
                        .requestMatchers(HttpMethod.PUT, "/api/members/{memberId}").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/items").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/items/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/mail").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/mail/check").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members/refresh-token").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // 로그인한 사용자만 접근 가능한 API (주문, 장바구니, 회원 정보)
                        .requestMatchers("/api/carts/**", "/api/orders/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/members/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/members/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/members/**").authenticated()
                        // ADMIN 등급만 접근 가능한 API (상품 등록, 수정, 삭제)
                        .requestMatchers(HttpMethod.POST, "/api/items").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/items/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/items/**").hasAuthority("ADMIN")
                        // oauth
//                        .requestMatchers(
//                                new AntPathRequestMatcher("/"),
//                                new AntPathRequestMatcher("/auth/success"),
//                                new AntPathRequestMatcher("/auth/failure"),
//                                new AntPathRequestMatcher("/auth/login/kakao/**")
//                        ).permitAll()
                        .requestMatchers(allowUrls).permitAll()  // 허용 URL 설정
                        // 그 외 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // oauth2 설정
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")  // 선택 사항: 로그인 페이지 커스터마이즈
                        .authorizationEndpoint(authorization ->
                                authorization.baseUri("/oauth2/authorization"))  // 인증 엔드포인트 설정
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)  // 로그인 성공 핸들러 설정
                )

//                .oauth2Login(oauth -> // OAuth2 로그인 기능에 대한 여러 설정의 진입점
//                        // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 담당
//                        oauth.userInfoEndpoint(c -> c.userService(oAuth2UserService))
//                                // 로그인 성공 시 핸들러
//                                .successHandler(oAuth2SuccessHandler)
//                )
//                .oauth2Login(oauth2 -> oauth2
//                        .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/**"))
//                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))  // OAuth2UserService 설정
//                )

                .formLogin(AbstractHttpConfigurer::disable)  // 기본 로그인 폼 비활성화
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 사용 안 함 (JWT 인증)
//                .sessionManagement(c ->
//                c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음
                // jwt 관련 설정
//                .addFilterBefore(jwtAuthenticationFilter,
//                        UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(JwtAuthenticationFilter(), jwtAuthenticationFilter.getClass()) // 토큰 예외 핸들링
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  // 인증 필터 추가
                // 인증 예외 핸들링
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler()))
                .logout(AbstractHttpConfigurer::disable);  // 로그아웃 비활성화


        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();  // 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 등록
    }

    @Bean
    public UserDetailsService userDetailsService(MemberRepository memberRepository) {
        return new CustomUserDetailsService(memberRepository);  // CustomUserDetailsService 빈 등록
    }

    @Bean // 인증 처리를 담당하는 핵심 컴포넌트
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();  // AuthenticationManager 빈 등록
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);  // CustomUserDetailsService 설정 > 이거 넣으니까 오류 해결
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());  // 비밀번호 암호화 설정
        return authProvider;
    }

//    /** OAuth2 인증을 위한 별도의 UserDetailsService 사용 */
//    @Bean
//    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
//        return new DefaultOAuth2UserService();
//    }
//    @Bean
//    public OAuth2LoginAuthenticationFilter oAuth2LoginAuthenticationFilter() {
//        return new OAuth2LoginAuthenticationFilter(oAuth2UserService());
//    }
}
