package com.aloc.aloc.coin.service;

import com.aloc.aloc.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CoinService {
  private final UserService userService;

  private static final int SOLVE_REWARD = 10;
  private static final int COURSE_REWARD = 20;
  private static final int STREAK_REWARD = 50;

  // 문제 해결시 10코인
  // 코스 해결시 코스 문제수 * 20개
  // 연속일 수 %7 == 0 일때 연속일 수 // 7 * 50개

  //	public CoinResponseDto giveCoinBySolvingProblem(User user) {
  //		userService.updateUserCoin(user, SOLVE_REWARD);
  //	}
}
