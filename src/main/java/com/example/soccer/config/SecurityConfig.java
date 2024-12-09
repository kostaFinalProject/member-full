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
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
//import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
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
            "/oauth2/**", // > http://localhost:8080/oauth2/authorization/kakao 허용
            "/",
            "/auth/login/kakao/**",
            "/auth/failure",
            "/auth/success",
            "/oauth2/authorization/kakao"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, customUserDetailsService);
        System.out.println("JWT Authentication Filter initialized: " + jwtAuthenticationFilter);
        http
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
//                        config.setAllowedOrigins(Collections.singletonList("http://127.0.0.1:5500"));
                        config.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500", "https://kauth.kakao.com"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L); //1시간
                        return config;
                    }
                }))
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 로그인 비활성화
                .headers(c -> c.frameOptions(
                        FrameOptionsConfig::disable).disable()) // X-Frame-Options 비활성화
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
                        .requestMatchers(
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/auth/success"),
                                new AntPathRequestMatcher("/auth/failure"),
                                new AntPathRequestMatcher("/auth/login/kakao/**"),
                                new AntPathRequestMatcher("/funding-products/**", "GET"),
                                new AntPathRequestMatcher("/notification/subscribe")
                        ).permitAll()
                        .requestMatchers(allowUrls).permitAll()  // 허용 URL 설정
                        // 그 외 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth ->
                        oauth
                                .loginPage("/login")  // 기본 로그인 페이지 (필요한 경우 커스터마이징)
                                .userInfoEndpoint(c -> c.userService(oAuth2UserService))
                                .successHandler(oAuth2SuccessHandler)
                                .failureHandler(new OAuth2FailureHandler())
                )
//                .oauth2Login(oauth -> oauth
//                        .userInfoEndpoint(userInfo -> {
//                            System.out.println("Configuring OAuth2 User Service: " + oAuth2UserService);
//                            userInfo.userService(oAuth2UserService);
//                        })
//                        .successHandler(oAuth2SuccessHandler)
////                        .failureHandler(oAuth2FailureHandler)
//                )
//                .oauth2Login(oauth -> oauth
//                        .userInfoEndpoint(c -> c.userService(oAuth2UserService))
//                        .successHandler((request, response, authentication) -> {
//                            // 인증 후 프론트엔드 페이지로 리디렉션
//                            response.sendRedirect("http://127.0.0.1:5500/main.html");
//                        })
//                        .failureHandler(new OAuth2FailureHandler())
//                );
                // oauth2 설정
//                .oauth2Login(oauth -> oauth
//                        .loginPage("/login")  // 선택 사항: 로그인 페이지 커스터마이즈
//                        .authorizationEndpoint(authorization ->
//                                authorization.baseUri("/oauth2/authorization"))  // 인증 엔드포인트 설정
//                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
//                        .successHandler(oAuth2SuccessHandler)  // 로그인 성공 핸들러 설정
//                )
//                .oauth2Login(oauth2 -> oauth2
//                        .redirectionEndpoint(endpoint->endpoint.baseUri("/oauth2/callback"))
//                        .userInfoEndpoint(endpoint->endpoint.userService(oAuth2UserService)))
//                .oauth2Login(oauth -> oauth
//                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
//                        .successHandler(oAuth2SuccessHandler)
//                )
//                .oauth2Login(oauth -> oauth// OAuth2 로그인 기능에 대한 여러 설정의 진입점
//
//                        // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 담당
//                        .loginPage("/login")
//                        .userInfoEndpoint(c -> c.userService(oAuth2UserService)) // // 사용자 정보를 처리하는 서비스 설정
//                        // 로그인 성공 시 핸들러
//                        .successHandler(oAuth2SuccessHandler)
//                )
//                .formLogin(AbstractHttpConfigurer::disable)  // 기본 로그인 폼 비활성화
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 사용 안 함 (JWT 인증)
//                .sessionManagement(c ->
//                c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음
                // jwt 관련 설정
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  // 인증 필터 추가
                .addFilterBefore(new TokenExceptionFilter(), jwtAuthenticationFilter.getClass()) // 토큰 예외 핸들링
                // 인증 예외 핸들링
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                .logout(AbstractHttpConfigurer::disable);  // 로그아웃 비활성화
        return http.build();
    } // 클라이언트 측에서 직접 카카오 액세스 토큰을 받아 서버로 전달하는 방식 대신, Spring Security에서 제공하는 oauth2Login()을 사용하면,
    // 서버가 자동으로 카카오 OAuth2 인증을 처리하고 액세스 토큰을 관리

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();  // 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 등록
    }

    // ClientRegistrationRepository는 OAuth2 클라이언트 정보를 저장하는 저장소입니다.
    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        // InMemoryOAuth2AuthorizedClientService를 사용하여 OAuth2AuthorizedClientService 빈 등록
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }
//    @Bean
//    public OAuth2AuthorizedClientService authorizedClientService(
//            ClientRegistrationRepository clientRegistrationRepository,
//            OAuth2AuthorizedClientRepository authorizedClientRepository) {
//        return new DefaultOAuth2AuthorizedClientService(clientRegistrationRepository, authorizedClientRepository);
//    }

//    @Bean
//    public UserDetailsService userDetailsService(MemberRepository memberRepository) {
//        return new CustomUserDetailsService(memberRepository);  // CustomUserDetailsService 빈 등록
//    }
//
//    @Bean // 인증 처리를 담당하는 핵심 컴포넌트
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();  // AuthenticationManager 빈 등록
//    }
//
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(customUserDetailsService);  // CustomUserDetailsService 설정 > 이거 넣으니까 오류 해결
//        authProvider.setPasswordEncoder(bCryptPasswordEncoder());  // 비밀번호 암호화 설정
//        return authProvider;
//    }
//
////    /** OAuth2 인증을 위한 별도의 UserDetailsService 사용 */
//    @Bean
//    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(MemberRepository memberRepository) {
//        return new CustomOAuth2UserService(memberRepository);  // MemberRepository를 전달
//    }
//    @Bean
//    public OAuth2LoginAuthenticationFilter oAuth2LoginAuthenticationFilter() {
//        return new OAuth2LoginAuthenticationFilter(oAuth2UserService());
//    }
}
