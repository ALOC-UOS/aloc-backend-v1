package com.aloc.aloc.user.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

  @Nested
  @DisplayName("문제 풀이 기록 업데이트 테스트")
  class SolvingProblemTest {

    @Test
    @DisplayName("첫 문제 풀이 시 연속 일수가 1로 설정됨")
    void updateUserBySolvingProblemFirstSolve() {
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
    @DisplayName("어제 풀었다면 연속 일수가 증가함")
    void updateUserBySolvingProblemConsecutiveSolve() {
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
    @DisplayName("연속이 깨졌을 때(2일 이상 간격) 연속 일수가 1로 초기화됨")
    void updateUserBySolvingProblemResetStreak() {
      // given
      User user = createTestUser();
      user.setLastSolvedAt(LocalDateTime.now().minusDays(3)); // 3일 전
      user.setConsecutiveSolvedDays(5);
      user.setSolvedCount(10);

      // when
      user.updateUserBySolvingProblem();

      // then
      assertThat(user.getSolvedCount()).isEqualTo(11);
      assertThat(user.getConsecutiveSolvedDays()).isEqualTo(1); // 초기화됨
    }

    @Test
    @DisplayName("같은 날 여러 번 풀어도 연속 일수는 증가하지 않음")
    void updateUserBySolvingProblemSameDay() {
      // given
      User user = createTestUser();
      user.setLastSolvedAt(LocalDateTime.now().minusHours(2)); // 오늘 2시간 전
      user.setConsecutiveSolvedDays(3);
      user.setSolvedCount(5);

      // when
      user.updateUserBySolvingProblem();

      // then
      assertThat(user.getSolvedCount()).isEqualTo(6);
      assertThat(user.getConsecutiveSolvedDays()).isEqualTo(1); // 같은 날이므로 초기화됨
    }
  }

  private User createTestUser() {
    return User.builder()
        .oauthId("test_oauth_id")
        .name("tester123123")
        .email("test@example.com")
        .profileImageFileName("test.jpg")
        .build();
  }
}
