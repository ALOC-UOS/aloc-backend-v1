package com.aloc.aloc.global.config;

import com.aloc.aloc.global.jwt.filter.JwtAuthenticationProcessingFilter;
import com.aloc.aloc.global.jwt.service.JwtServiceImpl;
import com.aloc.aloc.global.login.handler.LoginFailureHandler;
import com.aloc.aloc.global.login.service.UserDetailsServiceImpl;
import com.aloc.aloc.user.repository.UserRepository;
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
import org.springframework.security.oauth2.core.user.OAuth2User;
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
                        "/course",
                        "/users",
                        "/oauth/callback")
                    .permitAll()
                    .requestMatchers("/courses", "/user", "/user/**")
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
                          // ✅ Refresh Token 삭제 로직 추가
                          jwtService
                              .extractRefreshToken(request)
                              .ifPresent(jwtService::destroyRefreshToken);
                          response.setStatus(HttpServletResponse.SC_OK);
                        })
                    .invalidateHttpSession(true) // 세션 무효화 (JWT 기반이므로 사실상 필요 없음)
            )
        .oauth2Login(
            oauth2 ->
                oauth2
                    .successHandler(
                        (request, response, authentication) -> {
                          OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                          String oauthId = oAuth2User.getAttribute("sub");

                          String accessToken = jwtService.createAccessToken(oauthId);
                          String refreshToken = jwtService.createRefreshToken();

                          jwtService.updateRefreshToken(oauthId, refreshToken);

                          // ✅ Access & Refresh Token 설정
                          jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

                          // ✅ 프론트엔드로 리디렉트
                          // ✅ 요청의 오리진 가져오기 (예: https://www.openaloc.store)
                          String origin = request.getHeader("Origin");
                          if (origin == null) {
                            origin = "https://openaloc.store"; // 기본값 설정
                          }

                          // ✅ 동적 리다이렉트
                          String targetUrl = origin + "/finish-google-sso";
                          response.sendRedirect(targetUrl);
                        })
                    .failureHandler(
                        (request, response, exception) -> {
                          // 로그인 실패 시 로그 남기기
                          log.error("OAuth2 로그인 실패: {}", exception.getMessage());
                          response.sendRedirect("https://openaloc.store/login?error");
                        }));

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
