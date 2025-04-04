package com.aloc.aloc.coin.service;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.entity.CoinHistory;
import com.aloc.aloc.coin.enums.CoinType;
import com.aloc.aloc.coin.repository.CoinHistoryRepository;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CoinService {
  private final UserService userService;
  private final CoinHistoryRepository coinHistoryRepository;

  private static final int SOLVE_REWARD = 10;
  private static final int COURSE_REWARD = 20;
  private static final int STREAK_REWARD = 50;

  // 문제 해결시 10코인
  // 코스 해결시 코스 문제수 * 20개
  // 연속일 수 %7 == 0 일때 연속일 수 // 7 * 50개

  @Transactional
  public void recordCoinHistory(User user, CoinResponseDto coinResponseDto) {
    coinHistoryRepository.save(CoinHistory.of(user, coinResponseDto));
  }

  @Transactional
  public CoinResponseDto giveCoinBySolvingProblem(User user) {
    CoinResponseDto coinResponseDto =
        CoinResponseDto.of(user.getCoin(), SOLVE_REWARD, CoinType.SOLVE_REWARD, "문제 해결");
    userService.updateUserCoin(user, SOLVE_REWARD);
    recordCoinHistory(user, coinResponseDto);
    return coinResponseDto;
  }

  @Transactional
  public CoinResponseDto giveCoinBySolvingCourse(User user, Course course) {
    int addedCoin = course.getProblemCnt() * COURSE_REWARD;
    CoinResponseDto coinResponseDto =
        CoinResponseDto.of(
            user.getCoin(), addedCoin, CoinType.COURSE_REWARD, course.getTitle() + "코스 해결 보상");
    userService.updateUserCoin(user, addedCoin);
    recordCoinHistory(user, coinResponseDto);
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
    recordCoinHistory(user, coinResponseDto);
    return coinResponseDto;
  }

  @Transactional
  public ProblemSolvedResponseDto giveCoinByCoinType(String oauthId, int coinType) {
    User user = userService.getUser(oauthId);
    List<CoinResponseDto> coinResponseDtos = new ArrayList<>();
    coinResponseDtos.add(giveCoinBySolvingProblem(user));

    if (coinType == 2 || coinType == 4) {
      coinResponseDtos.add(giveCoinByStreakDays(user));
    }

    if (coinType == 3 || coinType == 4) {
      Course sampleCourse =
          new Course(
              10000L, "테스트 코스", "테스트 코스입니다.", CourseType.DAILY, 10, 3, 8, 5, 2L, 10, 2L, null);
      coinResponseDtos.add(giveCoinBySolvingCourse(user, sampleCourse));
    }
    return ProblemSolvedResponseDto.success(coinType > 2, coinResponseDtos);
  }
}
