package com.aloc.aloc.global.config;

import com.aloc.aloc.auth.handler.OAuth2AuthenticationSuccessHandler;
import com.aloc.aloc.auth.service.CustomOAuth2UserService;
import com.aloc.aloc.global.jwt.filter.JwtAuthenticationProcessingFilter;
import com.aloc.aloc.global.jwt.service.JwtServiceImpl;
import com.aloc.aloc.global.login.handler.LoginFailureHandler;
import com.aloc.aloc.global.login.service.UserDetailsServiceImpl;
import com.aloc.aloc.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
  private final UserDetailsServiceImpl userDetailsService;
  private final UserRepository userRepository;
  private final JwtServiceImpl jwtService;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

  // 특정 HTTP 요청에 대한 웹 기반 보안 구성
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .addFilterBefore(
            jwtAuthenticationProcessingFilter(), LogoutFilter.class) // ✅ UsernamePassword 관련 필터 제거
        .authorizeHttpRequests(
            (authorize) ->
                authorize
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/api-docs/**",
                        "/api/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/oauth2/authorization/**",
                        "/algorithm/**",
                        "/users",
                        "/oauth/callback",
                        "/courses",
                        "/auth/refresh",
                        "/course")
                    .permitAll()
                    .requestMatchers(
                        "/user", "/user/**", "/course/**", "/auth/logout", "/problem/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .exceptionHandling(
            exceptionConfig ->
                exceptionConfig
                    .authenticationEntryPoint(
                        (request, response, authException) ->
                            response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Access Denied"))
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) ->
                            response.sendError(
                                HttpServletResponse.SC_FORBIDDEN, "Forbidden: Missing token")))
        .logout(
            logout ->
                logout
                    .logoutUrl("/auth/logout")
                    .logoutSuccessHandler(
                        (request, response, authentication) -> {
                          // ✅ Refresh Token 삭제 (DB에서)
                          jwtService
                              .extractRefreshToken(request)
                              .ifPresent(jwtService::destroyRefreshToken);

                          // ✅ Refresh Token 쿠키도 브라우저에서 삭제
                          Cookie cookie = new Cookie("refreshToken", null);
                          cookie.setHttpOnly(true);
                          cookie.setSecure(true); // HTTPS 환경이라면 true, 아니면 false
                          cookie.setPath("/"); // 생성 시와 동일한 path 필요
                          cookie.setMaxAge(0); // 즉시 만료되도록 설정
                          response.addCookie(cookie);

                          response.setStatus(HttpServletResponse.SC_OK);
                        }))
        .oauth2Login(
            oauth2 ->
                oauth2
                    .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                    .successHandler(oAuth2AuthenticationSuccessHandler));

    return http.build();
  }

  @Bean
  public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
    return new JwtAuthenticationProcessingFilter(jwtService, userRepository);
  }

  @Bean
  public AuthenticationFailureHandler loginFailureHandler() {
    return new LoginFailureHandler();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider provider = daoAuthenticationProvider();
    provider.setPasswordEncoder(new BCryptPasswordEncoder());
    return new ProviderManager(provider);
  }

  private DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(new BCryptPasswordEncoder());

    return provider;
  }

  // CORS 설정
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    corsConfiguration.setAllowedOriginPatterns(
        Arrays.asList(
            "http://localhost:3000",
            "https://openaloc.store", // ✅ 프론트엔드 도메인 추가
            "https://www.openaloc.store",
            "https://api.openaloc.store"));
    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
    corsConfiguration.setAllowedHeaders(List.of("*"));
    corsConfiguration.setAllowCredentials(true);

    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }
}
