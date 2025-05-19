package com.aloc.aloc.coin.service;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.enums.CoinType;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import org.springframework.stereotype.Component;

@Component
public class SolveProblemRewardPolicy implements CoinRewardPolicy {
  private static final int REWARD = 10;

  @Override
  public boolean supports(User user, UserCourseProblem userCourseProblem) {
    return true;
  }

  @Override
  public CoinResponseDto apply(User user, UserCourseProblem userCourseProblem) {
    return CoinResponseDto.of(
        user.getCoin(),
        REWARD,
        CoinType.SOLVE_REWARD,
        userCourseProblem.getProblem().getTitle() + "문제 해결 보상");
  }
}
