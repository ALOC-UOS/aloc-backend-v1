package com.aloc.aloc.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import com.aloc.aloc.profilebackgroundcolor.repository.ProfileBackgroundColorRepository;
import com.aloc.aloc.profilebackgroundcolor.service.ProfileBackgroundColorService;
import com.aloc.aloc.user.dto.response.UserColorChangeResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private ProfileBackgroundColorService profileBackgroundColorService;
  @Mock
  private ProfileBackgroundColorRepository profileBackgroundColorRepository;

  @InjectMocks
  private UserService userService;

  @Test
  @DisplayName("유효한 oauthId로 유저 조회 성공")
  void getUserSuccess() {
    // given
    String oauthId = "oauthId";
    User user = TestFixture.getMockUserByOauthId(oauthId);
    given(userRepository.findByOauthId(oauthId)).willReturn(Optional.of(user));

    // when
    User result = userService.getUser(oauthId);

    // then
    assertThat(result).isEqualTo(user);
  }

  @Test
  @DisplayName("존재하지 않는 oauthId 조회 시 IllegalArgumentException 발생")
  void getUserFail() {
    // given
    String invalidOauthId = "invalid_id";
    given(userRepository.findByOauthId(invalidOauthId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.getUser(invalidOauthId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("해당 사용자가 존재하지 않습니다.");
  }

  @Nested
  @DisplayName("색상 변경 테스트")
  class ChangeColorTest {

    @Test
    @DisplayName("코인이 충분할 때 색상 변경 성공")
    void changeColorSuccess() {
      // given
      User user = TestFixture.getMockNewUser();
      user.setCoin(150); // 충분한 코인이 있을 때

      String newColorName = "Red";
      ProfileBackgroundColor newColor = createMockProfileBackgroundColor(newColorName);

      given(profileBackgroundColorService.pickColor()).willReturn(newColorName);
      given(profileBackgroundColorRepository.findById(newColorName))
          .willReturn(Optional.of(newColor));

      // when
      UserColorChangeResponseDto result = userService.changeColor(user);

      // then
      assertThat(result).isNotNull();
      assertThat(result.getUserCoin()).isEqualTo(50); // 150 - 100 = 50
      assertThat(result.getColor().getName()).isEqualTo(newColorName);
      assertThat(user.getProfileColor()).isEqualTo(newColorName);
      verify(userRepository).save(user);
    }

    @Test
    @DisplayName("코인이 부족할 때 예외 발생")
    void changeColorInsufficientCoin() {
      // given
      User user = TestFixture.getMockNewUser();
      user.setCoin(50); // 코인이 부족할 때

      // when & then
      assertThatThrownBy(() -> userService.changeColor(user))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("코인이 부족합니다.");

      assertThat(user.getCoin()).isEqualTo(50); // 코인이 차감되지 않음
    }

    @Test
    @DisplayName("정확히 100 코인일 때 색상 변경 성공")
    void changeColorExactlyCoin() {
      //given
      User user = TestFixture.getMockNewUser();
      user.setCoin(100); // 정확히 100 코인일 때

      String newColorName = "Green";
      ProfileBackgroundColor newColor = createMockProfileBackgroundColor(newColorName);

      given(profileBackgroundColorService.pickColor()).willReturn(newColorName);
      given(profileBackgroundColorRepository.findById(newColorName))
          .willReturn(Optional.of(newColor));

      //when
      UserColorChangeResponseDto result = userService.changeColor(user);

      //then
      assertThat(result).isNotNull();
      assertThat(result.getUserCoin()).isEqualTo(0); // 100 - 100 = 0
      assertThat(user.getProfileColor()).isEqualTo(newColorName);
    }
  }

  @Nested
  @DisplayName("백준 아이디 중복 체크 테스트")
  class CheckBaekjoonIdTest {
    @Test
    @DisplayName("이미 존재하는 백준 아이디일 때 예외 발생")
    void checkBaekjoonIdAlreadyExists() {
      //given
      String existingBaekjoonId = "existing_id";
      given(userRepository.existsByBaekjoonId(existingBaekjoonId)).willReturn(true);

      // when & then
      assertThatThrownBy(() -> userService.checkBaekjoonId(existingBaekjoonId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이미 존재하는 백준 아이디 입니다.");
    }

    @Test
    @DisplayName("사용 가능한 백준 아이디일 때 예외 발생하지 않음")
    void checkBaekjoonIdAvailable() {
      // given
      String availableBaekjoonId = "available_id";
      given(userRepository.existsByBaekjoonId(availableBaekjoonId)).willReturn(false);

      // when & then
      assertThatCode(() -> userService.checkBaekjoonId(availableBaekjoonId))
          .doesNotThrowAnyException();
    }
  }

  private ProfileBackgroundColor createMockProfileBackgroundColor(String name) {
    return new ProfileBackgroundColor(
        name, "#FF0000", "#FF6666", "#FFAAAA", "#FFDDDD", "#FFFFFF", "gradient", 45);
  }
}