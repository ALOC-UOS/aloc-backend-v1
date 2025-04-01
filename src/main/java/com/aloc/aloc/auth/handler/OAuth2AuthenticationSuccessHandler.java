package com.aloc.aloc.auth.handler;

import com.aloc.aloc.global.jwt.service.JwtService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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
      throws IOException, ServletException {

    // authentication.getPrincipal()에서 oauthId 추출 (UserDetails 형태로 캐스팅)
    var oauthUser =
        (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
    String oauthId = oauthUser.getUsername();

    User user =
        userRepository
            .findByOauthId(oauthId)
            .orElseThrow(() -> new IllegalArgumentException("❌ 로그인한 사용자를 찾을 수 없습니다."));

    // 토큰 생성
    String accessToken = jwtService.createAccessToken(oauthId);
    String refreshToken = jwtService.createRefreshToken();

    // 리프레시 토큰 저장
    jwtService.updateRefreshToken(oauthId, refreshToken);
    jwtService.setRefreshTokenCookie(response, refreshToken);

    String redirectUri = "https://openaloc.store/finish-google-sso";

    // ✅ 쿼리파라미터로 토큰 전달 (주의: refreshToken은 보안상 권장 안됨!)
    String redirectWithToken = String.format("%s?accessToken=%s", redirectUri, accessToken);

    // ✅ 리다이렉트
    response.sendRedirect(redirectWithToken);
  }
}
