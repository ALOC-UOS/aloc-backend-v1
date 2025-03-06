package com.aloc.aloc.auth.enums;

import com.aloc.aloc.user.entity.UserOAuthProfile;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {
  GOOGLE(
      "google",
      attributes ->
          new UserOAuthProfile(
              String.valueOf(attributes.get("sub")),
              (String) attributes.get("name"),
              (String) attributes.get("email"),
              (String) attributes.get("picture"))),

  NAVER(
      "naver",
      attributes -> {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return new UserOAuthProfile(
            (String) response.get("id"),
            (String) response.get("name"),
            (String) response.get("email"),
            (String) response.get("profile_image"));
      }),

  KAKAO(
      "kakao",
      attributes -> {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return new UserOAuthProfile(
            (String) kakaoAccount.get("id"),
            (String) profile.get("nickname"),
            (String) kakaoAccount.get("email"),
            (String) profile.get("profile_image_url"));
      });

  private final String registrationId;
  private final Function<Map<String, Object>, UserOAuthProfile> userProfileFactory;

  OAuthAttributes(
      String registrationId, Function<Map<String, Object>, UserOAuthProfile> userProfileFactory) {
    this.registrationId = registrationId;
    this.userProfileFactory = userProfileFactory;
  }

  public static UserOAuthProfile extract(String registrationId, Map<String, Object> attributes) {
    return Arrays.stream(values())
        .filter(provider -> registrationId.equals(provider.registrationId))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new)
        .userProfileFactory
        .apply(attributes);
  }
}
