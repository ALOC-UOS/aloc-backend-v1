package com.aloc.aloc.global.login.handler;

import com.aloc.aloc.global.jwt.service.JwtService;
import com.aloc.aloc.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public class JwtProviderHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Override
  // 로그인 성공 시 JWT 토큰 발급, 로그 출력
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    String oauthId = extractOauthId(authentication);
    String accessToken = jwtService.createAccessToken(oauthId);
    String refreshToken = jwtService.createRefreshToken();
    jwtService.updateRefreshToken(oauthId, refreshToken);
    jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
    userRepository
        .findByOauthId(oauthId)
        .ifPresentOrElse(
            user -> user.updateRefreshToken(refreshToken),
            () -> log.error("로그인 성공. JWT 발급. DB에 사용자 정보 없음. githubId: {}", oauthId));
    log.info("로그인에 성공합니다. githubId: {}", oauthId);
    log.info("AccessToken 을 발급합니다. AccessToken: {}", accessToken.substring(0, 10) + "...");
    log.info("RefreshToken 을 발급합니다. RefreshToken: {}", refreshToken.substring(0, 10) + "...");

    Map<String, String> tokenMap = new HashMap<>();
    tokenMap.put("accessToken", accessToken);
    tokenMap.put("refreshToken", refreshToken);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Expires", "0");
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(new ObjectMapper().writeValueAsString(tokenMap));

    //		response.getWriter().write("success");
  }

  private String extractOauthId(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return userDetails.getUsername();
  }
}
