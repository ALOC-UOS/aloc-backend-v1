package com.aloc.aloc.auth.handler;

import com.aloc.aloc.global.jwt.service.JwtService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {

    DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
    String oauthId = oauthUser.getAttribute("sub");
    log.info("oauthId : {}", oauthId);

    // 토큰 생성
    String accessToken = jwtService.createAccessToken(oauthId);
    String refreshToken = jwtService.createRefreshToken();

    // 리프레시 토큰 저장
    jwtService.updateRefreshToken(oauthId, refreshToken);
    jwtService.setRefreshTokenCookie(response, refreshToken);

    // 요청 도메인 체크
    String host = request.getHeader("Host");
    String redirectUri;
    if (host != null) {
      if (host.contains("dashboard.openaloc.store")) {
        redirectUri = "https://dashboard.openaloc.store/finish-google-sso";
      } else if (host.contains("openaloc.store")) {
        redirectUri = "https://openaloc.store/finish-google-sso";
      } else {
        redirectUri = "http://localhost:3000/finish-google-sso";
      }
    } else {
      redirectUri = "http://localhost:3000/finish-google-sso";
    }

    // ✅ 쿼리파라미터로 토큰 전달 (주의: refreshToken은 보안상 권장 안됨!)
    String redirectWithToken = String.format("%s?accessToken=%s", redirectUri, accessToken);
    User user = userRepository.findByOauthId(oauthId).orElseThrow();
    log.info("✅ 저장 후 user의 refreshToken: {}", user.getRefreshToken());
    // ✅ 리다이렉트
    response.sendRedirect(redirectWithToken);
  }
}
