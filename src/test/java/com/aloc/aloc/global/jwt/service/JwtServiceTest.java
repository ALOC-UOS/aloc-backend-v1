package com.aloc.aloc.global.jwt.service;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class JwtServiceTest {
  @Autowired JwtService jwtService;
  @Autowired UserRepository userRepository;
  @Autowired EntityManager em;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access.header}")
  private String accessHeader;

  private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
  private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
  private static final String OAUTH_CLAIM = "oauthId";
  private static final String BEARER = "Bearer ";

  private String oauthId = "google_123456"; // ✅ 변경: OAuth2 ID 사용

  @BeforeEach
  public void init() {
    userRepository.deleteAll(); // ✅ 테스트 실행 전 기존 데이터 삭제
    em.flush();
    em.clear();
    User user =
        User.builder()
            .username("홍길동")
            .password("1234")
            .oauthId(oauthId) // ✅ 변경: oauthId 사용
            .baekjoonId("baekjoon")
            .studentId("20")
            .discordId("discord")
            .notionEmail("notion@uos.ac.kr")
            .build();
    userRepository.save(user);
    clear();
  }

  private void clear() {
    em.flush();
    em.clear();
  }

  private DecodedJWT getVerify(String token) {
    return JWT.require(HMAC512(secret)).build().verify(token);
  }

  @Test
  public void createAccessTokenCheckOauthIdIsEqual() throws Exception {
    // given, when
    String accessToken = jwtService.createAccessToken(oauthId);
    DecodedJWT verify = getVerify(accessToken);

    String subject = verify.getSubject();
    String findOauthId = verify.getClaim(OAUTH_CLAIM).asString();

    // then
    assertThat(findOauthId).isEqualTo(oauthId);
    assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
  }

  @Test
  public void createRefreshTokenCheckOauthIdIsNull() throws Exception {
    // given, when
    String refreshToken = jwtService.createRefreshToken();
    DecodedJWT verify = getVerify(refreshToken);
    String subject = verify.getSubject();
    String storedOauthId = verify.getClaim(OAUTH_CLAIM).asString();

    // then
    assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
    assertThat(storedOauthId).isNull();
  }

  @Test
  public void updateRefreshTokenCheckOauthIdIsEqual() throws Exception {
    // given
    String refreshToken = jwtService.createRefreshToken();
    jwtService.updateRefreshToken(oauthId, refreshToken);
    clear();
    Thread.sleep(3000);

    // when
    String reIssuedRefreshToken = jwtService.createRefreshToken();
    jwtService.updateRefreshToken(oauthId, reIssuedRefreshToken);
    clear();

    // then
    assertThrows(
        Exception.class, () -> userRepository.findByRefreshToken(refreshToken).orElseThrow());
    assertThat(userRepository.findByRefreshToken(reIssuedRefreshToken).get().getOauthId())
        .isEqualTo(oauthId);
  }

  @Test
  public void destroyRefreshTokenCheckRefreshTokenIsNull() throws Exception {
    // given
    String refreshToken = jwtService.createRefreshToken();
    jwtService.updateRefreshToken(oauthId, refreshToken);
    clear();

    // when
    jwtService.destroyRefreshToken(refreshToken);
    em.flush(); // ✅ 변경 사항을 강제 적용
    em.clear(); // ✅ 영속성 컨텍스트 초기화

    // then
    assertThat(userRepository.findByRefreshToken(refreshToken)).isEmpty();
  }

  @Test
  public void extractOauthIdCheckIsEqual() throws Exception {
    // given
    String accessToken = jwtService.createAccessToken(oauthId);
    String refreshToken = jwtService.createRefreshToken();
    HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

    String requestAccessToken =
        jwtService
            .extractAccessToken(httpServletRequest)
            .orElseThrow(() -> new Exception("토큰이 없습니다"));

    // when
    String extractOauthId =
        jwtService.extractOauthId(requestAccessToken).orElseThrow(() -> new Exception("토큰이 없습니다"));

    // then
    assertThat(extractOauthId).isEqualTo(oauthId);
  }

  @Test
  public void checkTokenValidation() throws Exception {
    // given
    String accessToken = jwtService.createAccessToken(oauthId);
    String refreshToken = jwtService.createRefreshToken();

    // when, then
    assertThat(jwtService.isTokenValid(accessToken)).isTrue();
    assertThat(jwtService.isTokenValid(refreshToken)).isTrue();
    assertThat(jwtService.isTokenValid(accessToken + "d")).isFalse();
  }

  // ✅ 토큰 전송 테스트를 위한 함수 (헤더 + 쿠키 설정)
  private HttpServletRequest setRequest(String accessToken, String refreshToken) {
    MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
    jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

    String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);

    // ✅ Refresh Token을 쿠키에서 가져오기
    Cookie refreshTokenCookie = mockHttpServletResponse.getCookie("refreshToken");
    String cookieRefreshToken = (refreshTokenCookie != null) ? refreshTokenCookie.getValue() : null;

    MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
    httpServletRequest.addHeader(accessHeader, BEARER + headerAccessToken);

    // ✅ 헤더 대신 쿠키에 Refresh Token 추가
    if (cookieRefreshToken != null) {
      Cookie cookie = new Cookie("refreshToken", cookieRefreshToken);
      httpServletRequest.setCookies(cookie);
    }

    return httpServletRequest;
  }
}
