package com.aloc.aloc.global.jwt.service;

import com.aloc.aloc.user.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
@Slf4j
public class JwtServiceImpl implements JwtService {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access.expiration}")
  private long accessTokenValidityInSeconds;

  @Value("${jwt.refresh.expiration}")
  private long refreshTokenValidityInSeconds;

  @Value("${jwt.access.header}")
  private String accessHeader;

  @Value("${jwt.refresh.header}")
  private String refreshHeader;

  private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
  private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
  private static final String USERNAME_CLAIM = "oauthId";
  private static final String BEARER = "Bearer ";

  private final UserRepository userRepository;
  private final EntityManager entityManager;

  @Override
  public String createAccessToken(String oauthId) {
    return JWT.create()
        .withSubject(ACCESS_TOKEN_SUBJECT)
        .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
        .withClaim(USERNAME_CLAIM, oauthId)
        .sign(Algorithm.HMAC512(secret));
  }

  @Override
  public String createRefreshToken() {
    return JWT.create()
        .withSubject(REFRESH_TOKEN_SUBJECT)
        .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds * 1000))
        .sign(Algorithm.HMAC512(secret));
  }

  @Override
  @Transactional
  public void updateRefreshToken(String oauthId, String refreshToken) {
    userRepository
        .findByOauthId(oauthId)
        .ifPresentOrElse(
            users -> {
              users.updateRefreshToken(refreshToken);
              userRepository.save(users);
              entityManager.flush();
              log.info("✅ [updateRefreshToken] refreshToken 저장 성공 - user: {}", users.getOauthId());
            },
            () -> {
              throw new RuntimeException("❌ 회원 조회 실패: oauthId = " + oauthId);
            });
  }

  @Override
  public void destroyRefreshToken(String refreshToken) {
    userRepository
        .findByRefreshToken(refreshToken)
        .ifPresent(
            user -> {
              user.destroyRefreshToken(); // ✅ DB에서 Refresh Token 삭제
              userRepository.save(user); // ✅ 변경사항 반영
            });
  }

  @Override
  public void sendAccessAndRefreshToken(
      HttpServletResponse response, String accessToken, String refreshToken) {

    log.info("🔹 sendAccessAndRefreshToken() 호출됨");
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json"); // ✅ JSON 응답 설정
    response.setCharacterEncoding("UTF-8");

    setAccessTokenHeader(response, accessToken);
    setRefreshTokenCookie(response, refreshToken);

    log.info("✅ Setting Access Token Header: '{}'", accessToken.trim());
    log.info("✅ Setting Refresh Token Header: '{}'", refreshToken.trim());
  }

  @Override
  public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
    response.setHeader(accessHeader, accessToken.trim());
  }

  @Override
  public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
    Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge((int) refreshTokenValidityInSeconds);
    refreshTokenCookie.setAttribute("SameSite", "Lax");
    response.addCookie(refreshTokenCookie);
  }

  @Override
  public void sendAccessToken(HttpServletResponse response, String accessToken) {
    response.setStatus(HttpServletResponse.SC_OK);

    setAccessTokenHeader(response, accessToken);
  }

  @Override
  public Optional<String> extractAccessToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(accessHeader))
        .filter(accessToken -> accessToken.startsWith(BEARER))
        .map(accessToken -> accessToken.replace(BEARER, ""));
  }

  @Override
  public Optional<String> extractRefreshToken(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return Optional.empty();
    }

    return Arrays.stream(request.getCookies())
        .filter(
            cookie -> "refreshToken".equals(cookie.getName())) // ✅ 쿠키 이름이 refreshHeader와 같은 경우 필터링
        .map(Cookie::getValue) // ✅ 쿠키 값 (Refresh Token) 추출
        .findFirst();
  }

  @Override
  public Optional<String> extractOauthId(String accessToken) {
    try {
      return Optional.ofNullable(
          JWT.require(Algorithm.HMAC512(secret))
              .build()
              .verify(accessToken)
              .getClaim(USERNAME_CLAIM)
              .asString());
    } catch (Exception e) {
      log.error(e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public boolean isTokenValid(String token) {
    try {
      JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
      return true;
    } catch (Exception e) {
      log.error("유효하지 않은 Token입니다", e.getMessage());
      return false;
    }
  }
}
