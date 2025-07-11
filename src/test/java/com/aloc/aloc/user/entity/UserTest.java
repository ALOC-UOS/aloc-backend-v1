package com.aloc.aloc.user.entity;

import static org.assertj.core.api.Assertions.*;

import com.aloc.aloc.user.enums.Authority;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

  @Nested
  @DisplayName("리프레시 토큰 관리 테스트")
  class RefreshTokenTest {

    @Test
    @DisplayName("리프레시 토큰 업데이트 성공")
    void updateRefreshToken() {
      // given
      User user = createTestUser();
      String newRefreshToken = "new_refresh_token_123";

      // when
      user.updateRefreshToken(newRefreshToken);

      // then
      assertThat(user.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("리프레시 토큰 삭제 성공")
    void destroyRefreshToken() {
      // given
      User user = createTestUser();
      user.updateRefreshToken("some_token");
      assertThat(user.getRefreshToken()).isNotNull();

      // when
      user.destroyRefreshToken();

      // then
      assertThat(user.getRefreshToken()).isNull();
    }
  }

  @Nested
  @DisplayName("문제 풀이 기록 업데이트 테스트")
  class SolvingProblemTest {

    @Test
    @DisplayName("첫 문제 풀이 시 연속 일수가 1로 설정")
    void updateUserBySolvingProblem_FirstSolve() {
      // given
      User user = createTestUser();
      assertThat(user.getSolvedCount()).isZero();
      assertThat(user.getConsecutiveSolvedDays()).isZero();
      assertThat(user.getLastSolvedAt()).isNull();

      // when
      user.updateUserBySolvingProblem();

      // then
      assertThat(user.getSolvedCount()).isEqualTo(1);
      assertThat(user.getConsecutiveSolvedDays()).isEqualTo(1);
      assertThat(user.getLastSolvedAt()).isNotNull();
    }

    @Test
    @DisplayName("연속으로 문제를 풀 때 연속 일수 증가")
    void updateUserBySolvingProblem_ConsecutiveSolve() {
      // given
      User user = createTestUser();
      user.setLastSolvedAt(LocalDateTime.now().minusDays(1));
      user.setConsecutiveSolvedDays(3);
      user.setSolvedCount(5);

      // when
      user.updateUserBySolvingProblem();

      // then
      assertThat(user.getSolvedCount()).isEqualTo(6);
      assertThat(user.getConsecutiveSolvedDays()).isEqualTo(4);
    }

    @Test
    @DisplayName("연속성이 깨졌을 때 연속 일수가 1로 초기화")
    void updateUserBySolvingProblem_ResetStreak() {
      // given
      User user = createTestUser();
      user.setLastSolvedAt(LocalDateTime.now().minusDays(3)); // 3일 전
      user.setConsecutiveSolvedDays(5);
      user.setSolvedCount(10);

      // when
      user.updateUserBySolvingProblem();

      // then
      assertThat(user.getSolvedCount()).isEqualTo(11);
      assertThat(user.getConsecutiveSolvedDays()).isEqualTo(1); // 초기화
    }
  }

  @Nested
  @DisplayName("사용자 생성 테스트")
  class CreateUserTest {

    @Test
    @DisplayName("OAuth 프로필로 사용자 생성 성공")
    void create() {
      // given
      UserOAuthProfile oauthProfile = new UserOAuthProfile(
          "oauth123", "testUser", "test@example.com", "profile.jpg"
      );

      // when
      User user = User.create(oauthProfile);

      // then
      assertThat(user.getOauthId()).isEqualTo("oauth123");
      assertThat(user.getName()).isEqualTo("testUser");
      assertThat(user.getEmail()).isEqualTo("test@example.com");
      assertThat(user.getAuthority()).isEqualTo(Authority.ROLE_NEW_USER);
      assertThat(user.getCoin()).isZero();
      assertThat(user.getProfileColor()).isEqualTo("Blue");
    }

    @Test
    @DisplayName("빌더로 사용자 생성 시 기본값 설정 확인")
    void createWithBuilder() {
      // when
      User user = User.builder()
          .oauthId("oauth456")
          .name("빌더유저")
          .email("builder@test.com")
          .profileImageFileName("builder.jpg")
          .build();

      // then
      assertThat(user.getAuthority()).isEqualTo(Authority.ROLE_NEW_USER);
      assertThat(user.getCoin()).isZero();
      assertThat(user.getProfileColor()).isEqualTo("Blue");
      assertThat(user.getSolvedCount()).isZero();
      assertThat(user.getConsecutiveSolvedDays()).isZero();
    }
  }

  @Nested
  @DisplayName("코인 관리 테스트")
  class CoinTest {

    @Test
    @DisplayName("코인 추가 성공")
    void addCoin() {
      // given
      User user = createTestUser();
      user.setCoin(100);

      // when
      user.addCoin(50);

      // then
      assertThat(user.getCoin()).isEqualTo(150);
    }

    @Test
    @DisplayName("음수 코인 추가 시 차감")
    void addNegativeCoin() {
      // given
      User user = createTestUser();
      user.setCoin(100);

      // when
      user.addCoin(-30);

      // then
      assertThat(user.getCoin()).isEqualTo(70);
    }

    @Test
    @DisplayName("0 코인 추가 시 변화 없음")
    void addZeroCoin() {
      // given
      User user = createTestUser();
      user.setCoin(100);

      // when
      user.addCoin(0);

      // then
      assertThat(user.getCoin()).isEqualTo(100);
    }
  }

  private User createTestUser() {
    return User.builder()
        .oauthId("test_oauth_id")
        .name("테스트유저")
        .email("test@example.com")
        .profileImageFileName("test.jpg")
        .build();
  }
}
