package com.aloc.aloc.coin.service;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.enums.CoinType;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional
  public CoinResponseDto giveCoinBySolvingProblem(User user) {
    CoinResponseDto coinResponseDto =
        CoinResponseDto.of(user.getCoin(), SOLVE_REWARD, CoinType.SOLVE_REWARD, "문제 해결");
    userService.updateUserCoin(user, SOLVE_REWARD);
    return coinResponseDto;
  }

  @Transactional
  public CoinResponseDto giveCoinBySolvingCourse(User user, Course course) {
    int addedCoin = course.getProblemCnt() * COURSE_REWARD;
    CoinResponseDto coinResponseDto =
        CoinResponseDto.of(
            user.getCoin(), addedCoin, CoinType.COURSE_REWARD, course.getTitle() + "코스 해결 보상");
    userService.updateUserCoin(user, addedCoin);
    return coinResponseDto;
  }

  @Transactional
  public CoinResponseDto giveCoinByStreakDays(User user) {
    int streakDays = user.getConsecutiveSolvedDays();

    int addedCoin = (streakDays / 7) * STREAK_REWARD;
    CoinResponseDto coinResponseDto =
        CoinResponseDto.of(
            user.getCoin(), addedCoin, CoinType.STREAK_REWARD, "연속 " + streakDays + "일 문제 해결 보상");
    userService.updateUserCoin(user, addedCoin);
    return coinResponseDto;
  }
}
