package com.aloc.aloc.coin.service;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;

public interface CoinRewardPolicy {
  boolean supports(User user, UserCourseProblem userCourseProblem); // 실행 여부

  CoinResponseDto apply(User user, UserCourseProblem userCourseProblem);
}
