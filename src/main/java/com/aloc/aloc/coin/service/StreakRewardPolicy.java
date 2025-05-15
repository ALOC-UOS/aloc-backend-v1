package com.aloc.aloc.coin.service;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.enums.CoinType;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import org.springframework.stereotype.Component;

@Component
public class StreakRewardPolicy implements CoinRewardPolicy {
  private static final int STREAK_REWARD = 50;

  @Override
  public boolean supports(User user, UserCourseProblem userCourseProblem) {
    return user.getConsecutiveSolvedDays() % 7 == 0;
  }

  @Override
  public CoinResponseDto apply(User user, UserCourseProblem userCourseProblem) {
    int added = (user.getConsecutiveSolvedDays() / 7) * STREAK_REWARD;
    return CoinResponseDto.of(
        user.getCoin(),
        added,
        CoinType.STREAK_REWARD,
        "연속 " + user.getConsecutiveSolvedDays() + "일 문제 해결 보상");
  }
}
