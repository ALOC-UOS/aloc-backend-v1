package com.aloc.aloc.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

  private final OAuth2AuthorizationRequestResolver defaultResolver;

  public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo) {
    this.defaultResolver =
        new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
  }

  @Override
  public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
    OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
    return customize(request, req);
  }

  @Override
  public OAuth2AuthorizationRequest resolve(
      HttpServletRequest request, String clientRegistrationId) {
    OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
    return customize(request, req);
  }

  private OAuth2AuthorizationRequest customize(
      HttpServletRequest request, OAuth2AuthorizationRequest req) {
    if (req == null) return null;

    String state = request.getParameter("state");
    return OAuth2AuthorizationRequest.from(req)
        .state(state) // 이 state가 로그인 성공 후 그대로 돌아옴
        .build();
  }
}
