package com.aloc.aloc.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import com.aloc.aloc.profilebackgroundcolor.repository.ProfileBackgroundColorRepository;
import com.aloc.aloc.profilebackgroundcolor.service.ProfileBackgroundColorService;
import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.user.dto.response.UserColorChangeResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private BaekjoonRankScrapingService baekjoonRankScrapingService;
  @Mock private ProfileBackgroundColorService profileBackgroundColorService;
  @Mock private ProfileBackgroundColorRepository profileBackgroundColorRepository;

  @InjectMocks private UserService userService;

  @Nested
  @DisplayName("유저 조회 테스트")
  class GetUserTest {

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

    @Test
    @DisplayName("유효한 UUID로 유저 조회 성공")
    void getUserByUUIDSuccess() {
      // given
      UUID userId = UUID.randomUUID();
      User mockUser = TestFixture.getMockNewUser();
      given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

      // when
      User result = userService.getUserByUUID(userId);

      // then
      assertThat(result).isEqualTo(mockUser);
    }
  }

  @Nested
  @DisplayName("유저 목록 조회 테스트")
  class GetUserListTest {

    @Test
    @DisplayName("활성화된 모든 유저 목록 조회")
    void getActiveUsersSuccess() {
      // given
      Set<Authority> activeAuthorities = Set.of(Authority.ROLE_USER, Authority.ROLE_ADMIN);
      given(userRepository.findAllByAuthorityIn(activeAuthorities))
          .willReturn(List.of(mock(User.class)));
      // when
      List<User> result = userService.getActiveUsers();
      // then
      assertThat(result).hasSize(1);
      verify(userRepository, times(1)).findAllByAuthorityIn(activeAuthorities);
    }

    @Test
    @DisplayName("모든 유저 목록 조회")
    void findAllUsersSuccess() {
      // given
      given(userRepository.findAll()).willReturn(List.of(mock(User.class), mock(User.class)));
      // when
      List<User> result = userService.findAllUsers();
      // then
      assertThat(result).hasSize(2);
      verify(userRepository, times(1)).findAll();
    }
  }

  @Nested
  @DisplayName("유저 상태 변경 테스트")
  class UpdateUserStatusTest {

    @Test
    @DisplayName("로그아웃 시 리프레시 토큰 제거")
    void logoutSuccess() {
      // given
      String oauthId = "test_user";
      User mockUser = TestFixture.getMockUserByOauthId(oauthId);
      mockUser.updateRefreshToken("some_token");
      given(userRepository.findByOauthId(oauthId)).willReturn(Optional.of(mockUser));
      // when
      userService.logout(oauthId);
      // then
      assertThat(mockUser.getRefreshToken()).isNull();
      verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    @DisplayName("연속 출석일 초기화 로직 검증")
    void initializeUserStreakDays() {
      // given
      User user1 = TestFixture.getMockNewUser(); // 어제 푼 기록 없음
      user1.setLastSolvedAt(LocalDateTime.now().minusDays(2));
      user1.setConsecutiveSolvedDays(5);

      User user2 = TestFixture.getMockNewUser(); // 어제 푼 기록 있음
      user2.setLastSolvedAt(LocalDateTime.now().minusDays(1));
      user2.setConsecutiveSolvedDays(3);

      given(userRepository.findAllByAuthorityIn(anySet())).willReturn(List.of(user1, user2));
      // when
      userService.initializeUserStreakDays();
      // then
      assertThat(user1.getConsecutiveSolvedDays()).isZero(); // 초기화 되어야 함
      assertThat(user2.getConsecutiveSolvedDays()).isEqualTo(3); // 유지 되어야 함
    }

    @Test
    @DisplayName("백준 랭크 정보 업데이트")
    void updateUserBaekjoonRank() {
      // given
      User user = TestFixture.getMockNewUser();
      user.setBaekjoonId("test_baekjoon");
      given(baekjoonRankScrapingService.extractBaekjoonRank("test_baekjoon")).willReturn(25);
      // when
      userService.updateUserBaekjoonRank(user);
      // then
      assertThat(user.getRank()).isEqualTo(25);
    }
  }

  @Nested
  @DisplayName("관리자 기능 테스트")
  class AdminFunctionTest {

    @Test
    @DisplayName("관리자 권한 검증 성공")
    void validateAdminSuccess() {
      // given
      User admin = TestFixture.getMockNewUser();
      admin.setAuthority(Authority.ROLE_ADMIN);
      given(userRepository.findByOauthId("admin_user")).willReturn(Optional.of(admin));
      // when & then
      assertThatCode(() -> userService.validateAdmin("admin_user")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("일반 유저가 관리자 기능 접근 시 예외 발생")
    void validateAdminFail() {
      // given
      User user = TestFixture.getMockNewUser();
      user.setAuthority(Authority.ROLE_USER);
      given(userRepository.findByOauthId("normal_user")).willReturn(Optional.of(user));
      // when & then
      assertThatThrownBy(() -> userService.validateAdmin("normal_user"))
          .isInstanceOf(IllegalStateException.class)
          .hasMessage("관리자만 이용가능한 서비스입니다.");
    }
  }

  @Nested
  @DisplayName("백준 ID 체크 테스트")
  class CheckBaekjoonIdTest {

    @Test
    @DisplayName("존재하지 않는 백준 ID는 예외 미발생")
    void checkNotExistingBaekjoonId() {
      // given
      String baekjoonId = "new_baekjoon_id";
      given(userRepository.existsByBaekjoonId(baekjoonId)).willReturn(false);

      // when & then
      assertThatCode(() -> userService.checkBaekjoonId(baekjoonId)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이미 존재하는 백준 ID는 IllegalArgumentException 발생")
    void checkExistingBaekjoonId() {
      // given
      String baekjoonId = "existing_baekjoon_id";
      given(userRepository.existsByBaekjoonId(baekjoonId)).willReturn(true);

      // when & then
      assertThatThrownBy(() -> userService.checkBaekjoonId(baekjoonId))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("이미 존재하는 백준 아이디 입니다.");
    }
  }

  @Nested
  @DisplayName("프로필 색상 변경 테스트")
  class ChangeColorTest {

    @Test
    @DisplayName("코인이 충분할 때 색상 변경 성공")
    void changeColorSuccess() {
      // given
      User user = TestFixture.getMockUserByOauthId("test_user");
      user.setCoin(200);

      ProfileBackgroundColor mockColor =
          new ProfileBackgroundColor("newColor", "#FFFFFF", null, null, null, null, "common", null);
      given(profileBackgroundColorService.pickColor()).willReturn("newColor");
      given(profileBackgroundColorRepository.findById("newColor"))
          .willReturn(Optional.of(mockColor));

      // when
      UserColorChangeResponseDto response = userService.changeColor(user);

      // then
      assertThat(user.getCoin()).isEqualTo(100);
      assertThat(user.getProfileColor()).isEqualTo("newColor");
      assertThat(response.getUserCoin()).isEqualTo(100);
      assertThat(response.getColor().getName()).isEqualTo("newColor");
    }

    @Test
    @DisplayName("코인이 부족할 때 IllegalArgumentException 발생")
    void changeColorFailNotEnoughCoin() {
      // given
      User user = TestFixture.getMockUserByOauthId("test_user");
      user.setCoin(50);

      // when & then
      assertThatThrownBy(() -> userService.changeColor(user))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("코인이 부족합니다.");
    }
  }

  @Nested
  @DisplayName("유저 코인 변경 테스트")
  class UpdateUserCoinTest {

    @Test
    @DisplayName("유저의 코인이 정상적으로 증가")
    void updateUserCoinSuccess() {
      // given
      User user = TestFixture.getMockNewUser();
      user.setCoin(100); // 초기 코인 100

      // when
      userService.updateUserCoin(user, 50);

      // then
      assertThat(user.getCoin()).isEqualTo(150); // 100 + 50 = 150
      verify(userRepository, times(1)).save(user);
    }
  }
}