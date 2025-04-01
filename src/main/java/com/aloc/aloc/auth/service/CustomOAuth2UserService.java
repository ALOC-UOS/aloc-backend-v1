package com.aloc.aloc.auth.service;

import com.aloc.aloc.auth.enums.OAuthAttributes;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.entity.UserOAuthProfile;
import com.aloc.aloc.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final UserRepository userRepository;
  private final EntityManager entityManager;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

    // ✅ user-info-uri 값 확인
    String userInfoEndpointUri =
        userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();

    log.info("🔍 UserInfo 요청 URI: {}", userInfoEndpointUri); // ✅ 올바른지 로그 확인
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    // OAuth 서비스 이름(ex. google, naver, kakao)
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    // OAuth2 서비스에서 제공하는 유저 정보
    Map<String, Object> attributes = oAuth2User.getAttributes();

    // OAuth2 서비스별 공통된 UserProfile 객체 생성
    UserOAuthProfile userOAuthProfile = OAuthAttributes.extract(registrationId, attributes);

    // 기존 사용자 조회 또는 신규 사용자 저장
    User user = getOrSaveUser(userOAuthProfile);

    return createDefaultOAuth2User(user, attributes);
  }

  private User getOrSaveUser(UserOAuthProfile userOAuthProfile) {
    User user = userRepository.findByOauthId(userOAuthProfile.oauthId()).orElse(null);
    if (user == null) {
      user = User.create(userOAuthProfile);
      userRepository.save(user);
      entityManager.flush();
    }
    return user;
  }

  private DefaultOAuth2User createDefaultOAuth2User(User user, Map<String, Object> attributes) {
    return new DefaultOAuth2User(
        Collections.singletonList(new SimpleGrantedAuthority(user.getAuthority().name())),
        attributes,
        "sub" // Google의 기본 값. 네이버, 카카오는 `OAuthAttributes.extract()`에서 자동 매핑됨
        );
  }
}
