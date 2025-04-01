package com.aloc.aloc.global.jwt.filter;

import com.aloc.aloc.global.jwt.service.JwtService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserRepository userRepository;

  private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException, java.io.IOException {

    // ✅ 기존: ID/PW 로그인용 URL → OAuth2 로그인 관련 URL로 수정
    String noCheckUrl = "/oauth2/authorization";
    String refreshTokenUrl = "/auth/refresh";

    // ✅ OAuth2 로그인 요청이면 필터 통과
    if (request.getRequestURI().startsWith(noCheckUrl)) {
      filterChain.doFilter(request, response);
      return;
    }

    if (request.getRequestURI().equals(refreshTokenUrl)) {
      Optional<String> refreshToken = extractRefreshToken(request);

      if (refreshToken.isPresent() && jwtService.isTokenValid(refreshToken.get())) {
        checkRefreshTokenAndReIssueAccessToken(response, refreshToken.get());
      } else {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        log.error(
            "리프레시 토큰이 없거나, 리프레시 토큰이 올바르지 않습니다. refresh Token is present : {}, is Token valid : {}",
            refreshToken.isPresent(),
            jwtService.isTokenValid(refreshToken.get()));
      }
      return;
    }

    // ✅ AccessToken 유효성 검사 후 필터 진행
    checkAccessTokenAndAuthentication(request, response, filterChain);
  }

  private Optional<String> extractRefreshToken(HttpServletRequest request) {
    return jwtService.extractRefreshToken(request); // ✅ 쿠키에서 가져오도록 변경
  }

  private void checkAccessTokenAndAuthentication(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException, java.io.IOException {

    boolean isAuthenticated =
        jwtService
            .extractAccessToken(request)
            .filter(jwtService::isTokenValid)
            .flatMap(
                accessToken ->
                    jwtService.extractOauthId(accessToken).flatMap(userRepository::findByOauthId))
            .map(
                user -> {
                  saveAuthentication(user);
                  return true;
                })
            .orElse(false);

    if (!isAuthenticated) {
      SecurityContextHolder.clearContext(); // ✅ 명시적으로 context 초기화
    }

    filterChain.doFilter(request, response);
  }

  private void saveAuthentication(User user) {
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getAuthority().toString());

    UserDetails userDetails =
        new org.springframework.security.core.userdetails.User(
            user.getOauthId(), "", Collections.singleton(grantedAuthority));

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            userDetails, null, authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

    SecurityContext context = SecurityContextHolder.createEmptyContext(); // 5
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
  }

  private void checkRefreshTokenAndReIssueAccessToken(
      HttpServletResponse response, String refreshToken) {
    ObjectMapper objectMapper = new ObjectMapper();

    userRepository
        .findByRefreshToken(refreshToken)
        .ifPresentOrElse(
            user -> {
              try {
                String newAccessToken = jwtService.createAccessToken(user.getOauthId());

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpStatus.OK.value());

                Map<String, String> tokenMap = new HashMap<>();
                tokenMap.put("accessToken", newAccessToken);

                response.getWriter().write(objectMapper.writeValueAsString(tokenMap));
              } catch (Exception e) {
                log.error(
                    "❗️AccessToken 재발급 중 예외 발생 - user: {}, error: {}",
                    user.getOauthId(),
                    e.getMessage(),
                    e);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
              }
            },
            () -> {
              log.warn("⚠️ 유효하지 않은 Refresh Token으로 요청 시도: {}", refreshToken);
              response.setStatus(HttpStatus.UNAUTHORIZED.value());
            });
  }
}
