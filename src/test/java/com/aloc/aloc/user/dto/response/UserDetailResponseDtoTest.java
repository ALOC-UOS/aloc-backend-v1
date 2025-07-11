package com.aloc.aloc.user.dto.response;

import static org.assertj.core.api.Assertions.*;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserDetailResponseDtoTest {

  @Test
  @DisplayName("User와 ProfileBackgroundColor로 UserDetailResponseDto 생성 성공")
  void of() {
    // given
    User user = TestFixture.getMockUserByOauthId("detailTestUser");
    user.setName("상세테스트유저");
    user.setBaekjoonId("detailBaekjoon");
    user.setRank(18);
    user.setCoin(200);
    user.setSolvedCount(85);
    user.setConsecutiveSolvedDays(12);
    user.setAuthority(Authority.ROLE_USER);
    user.setProfileImageFileName("detail-profile.png");

    ProfileBackgroundColor color = new ProfileBackgroundColor(
        "Orange", "#FF8C00", "#FFA500", "#FFB84D", "#FFCC80", "#FFE0B3", "gradient", 60);
    boolean isTodaySolved = true;

    // when
    UserDetailResponseDto result = UserDetailResponseDto.of(user, color, isTodaySolved);

    // then
    assertThat(result).isNotNull();
    // UserResponseDto 상속 필드들
    assertThat(result.getName()).isEqualTo("상세테스트유저");
    assertThat(result.getBaekjoonId()).isEqualTo("detailBaekjoon");
    assertThat(result.getRank()).isEqualTo(18);
    assertThat(result.getCoin()).isEqualTo(200);
    assertThat(result.getAuthority()).isEqualTo(Authority.ROLE_USER);
    assertThat(result.getProfileImageFileName()).isEqualTo("detail-profile.png");
    
    // UserDetailResponseDto 고유 필드들
    assertThat(result.getSolvedCount()).isEqualTo(85);
    assertThat(result.getConsecutiveSolvedDays()).isEqualTo(12);
    assertThat(result.isTodaySolved()).isTrue();
    assertThat(result.getColor()).isNotNull();
    assertThat(result.getColor().getName()).isEqualTo("Orange");
    assertThat(result.getCreatedAt()).isEqualTo(user.getCreatedAt());
  }

  @Test
  @DisplayName("오늘 문제를 풀지 않은 경우의 DTO 생성")
  void of_NotTodaySolved() {
    // given
    User user = TestFixture.getMockNewUser();
    user.setSolvedCount(0);
    user.setConsecutiveSolvedDays(0);
    
    ProfileBackgroundColor color = new ProfileBackgroundColor(
        "Blue", "#0066CC", "#0080FF", "#3399FF", "#66B3FF", "#99CCFF", "gradient", 45);
    boolean isTodaySolved = false;

    // when
    UserDetailResponseDto result = UserDetailResponseDto.of(user, color, isTodaySolved);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getSolvedCount()).isEqualTo(0);
    assertThat(result.getConsecutiveSolvedDays()).isEqualTo(0);
    assertThat(result.isTodaySolved()).isFalse();
  }

  @Test
  @DisplayName("null 값들이 포함된 경우의 DTO 생성")
  void of_WithNullValues() {
    // given
    User user = TestFixture.getMockNewUser();
    user.setName("NullDetailUser");
    user.setBaekjoonId(null);
    user.setRank(null);
    user.setSolvedCount(null);
    user.setConsecutiveSolvedDays(null);
    
    ProfileBackgroundColor color = new ProfileBackgroundColor(
        "Default", "#CCCCCC", null, null, null, null, "solid", null);
    boolean isTodaySolved = false;

    // when
    UserDetailResponseDto result = UserDetailResponseDto.of(user, color, isTodaySolved);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("NullDetailUser");
    assertThat(result.getBaekjoonId()).isNull();
    assertThat(result.getRank()).isNull();
    assertThat(result.getSolvedCount()).isNull();
    assertThat(result.getConsecutiveSolvedDays()).isNull();
    assertThat(result.isTodaySolved()).isFalse();
    assertThat(result.getColor().getName()).isEqualTo("Default");
  }
}
