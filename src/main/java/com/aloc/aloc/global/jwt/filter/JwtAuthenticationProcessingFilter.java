package com.aloc.aloc.global.jwt.filter;

import com.aloc.aloc.global.jwt.service.JwtService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserRepository userRepository;

  private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

  /**
   * 1. /refresh를 요청하는 경우 -> accessToken 재발급 2. 리프레시 토큰은 없고 AccessToken만 있는 경우 -> 유저정보 저장후 필터 계속 진행
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException, java.io.IOException {
    String noCheckUrl = "/api2/login";
    String refreshTokenUrl = "/api2/refresh";
    if (request.getRequestURI().equals(noCheckUrl)) {
      filterChain.doFilter(request, response);
      return;
    }
    String extractToken;
    if (request.getRequestURI().equals(refreshTokenUrl)) {
      try {
        extractToken = extractTokenFromJsonBody(request);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      if (jwtService.isTokenValid(extractToken)) {
        checkRefreshTokenAndReIssueAccessToken(response, extractToken);
      } else {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
      }
      return;
    }

    // AccessToken 유효성 검사 후 필터 진행
    checkAccessTokenAndAuthentication(request, response, filterChain);
  }

  private String extractTokenFromJsonBody(HttpServletRequest request) throws Exception {
    StringBuilder stringBuilder = new StringBuilder();
    BufferedReader bufferedReader = null;
    try {
      InputStream inputStream = request.getInputStream();
      if (inputStream != null) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        char[] charBuffer = new char[128];
        int bytesRead;
        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
          stringBuilder.append(charBuffer, 0, bytesRead);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (bufferedReader != null) {
        bufferedReader.close();
      }
    }

    String body = stringBuilder.toString();

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(body);

    // JSON에서 "refreshToken" 필드를 찾아 반환합니다.
    // 실제 JSON 구조에 따라 이 부분을 조정해야 할 수 있습니다.
    return jsonNode.get("refreshToken").asText();
  }

  private void checkAccessTokenAndAuthentication(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException, java.io.IOException {
    jwtService
        .extractAccessToken(request)
        .filter(jwtService::isTokenValid)
        .flatMap(
            accessToken ->
                jwtService.extractOauthId(accessToken).flatMap(userRepository::findByOauthId))
        .ifPresent(this::saveAuthentication);

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
                e.printStackTrace();
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
              }
            },
            () -> response.setStatus(HttpStatus.UNAUTHORIZED.value()));
  }
}
