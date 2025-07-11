package com.aloc.aloc.user.dto.response;

import static org.assertj.core.api.Assertions.*;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserResponseDtoTest {

  @Test
  @DisplayName("User 엔티티로부터 UserResponseDto 생성 성공")
  void of() {
    // given
    User user = TestFixture.getMockUserByOauthId("testUser");
    user.setName("테스트유저");
    user.setBaekjoonId("testBaekjoon");
    user.setRank(25);
    user.setCoin(150);
    user.setAuthority(Authority.ROLE_USER);
    user.setProfileImageFileName("test-profile.jpg");

    // when
    UserResponseDto result = UserResponseDto.of(user);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("테스트유저");
    assertThat(result.getBaekjoonId()).isEqualTo("testBaekjoon");
    assertThat(result.getRank()).isEqualTo(25);
    assertThat(result.getCoin()).isEqualTo(150);
    assertThat(result.getAuthority()).isEqualTo(Authority.ROLE_USER);
    assertThat(result.getProfileImageFileName()).isEqualTo("test-profile.jpg");
  }

  @Test
  @DisplayName("null 값들이 포함된 User로부터 DTO 생성")
  void of_WithNullValues() {
    // given
    User user = TestFixture.getMockNewUser();
    user.setName("NullTestUser");
    user.setBaekjoonId(null); // null 값
    user.setRank(null); // null 값
    user.setCoin(0);

    // when
    UserResponseDto result = UserResponseDto.of(user);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("NullTestUser");
    assertThat(result.getBaekjoonId()).isNull();
    assertThat(result.getRank()).isNull();
    assertThat(result.getCoin()).isEqualTo(0);
  }
}
