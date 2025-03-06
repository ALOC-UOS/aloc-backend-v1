package com.aloc.aloc.problem.service;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.coinhistory.enums.CoinType;
import com.aloc.aloc.coinhistory.service.CoinHistoryService;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.enums.UserProblemStatus;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.UserProblemRepository;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoinService {
  private final AlgorithmService algorithmService;
  private final UserProblemRepository userProblemRepository;
  private final ProblemRepository problemRepository;
  private final UserService userService;
  private final CoinHistoryService coinHistoryService;

  @Value("${app.season}")
  private Integer currentSeason;

  private static final int COINS_FOR_1ST_PLACE = 50;
  private static final int COINS_FOR_2ND_PLACE = 40;
  private static final int COINS_FOR_3RD_PLACE = 30;
  private static final int COINS_FOR_OTHERS = 20;
  private static final int COINS_FOR_FULL = 200;
  private static final int COINS_FOR_HALF = 100;

  public int calculateCoinToAddForDaily(Long problemId) {
    // daily문제 푼 순서 1등 50, 2등 40, 3등 30, 4등 이하 20 코인을 지급합니다.
    int solvedUserCount =
        userProblemRepository.countSolvingUsersByProblemId(
            problemId, currentSeason, UserProblemStatus.SOLVED);
    return getCoinsForPlace(solvedUserCount);
  }

  public int calculateCoinToAddForWeekly(Problem problem, User user) {
    Algorithm thisWeekAlgorithm =
        algorithmService
            .getWeeklyAlgorithmBySeason(currentSeason)
            .orElseThrow(() -> new RuntimeException("이번주 알고리즘이 존재하지 않습니다."));
    if (thisWeekAlgorithm.equals(problem.getAlgorithm())
        && getUnsolvedProblemCount(
                problemRepository.findAllByAlgorithmAndProblemType(
                    problem.getAlgorithm(), problem.getProblemType()),
                user.getId())
            == 0) {
      return getCoinsForCourse(problem.getProblemType().getCourse());
    }
    return 0;
  }

  private int getUnsolvedProblemCount(List<Problem> thisWeekProblems, Long userId) {
    return userProblemRepository.countByUnsolvedProblemsIn(
        thisWeekProblems, userId, UserProblemStatus.UNSOLVED);
  }

  private int getCoinsForPlace(int solvedUserCount) {
    return switch (solvedUserCount) {
      case 0 -> COINS_FOR_1ST_PLACE;
      case 1 -> COINS_FOR_2ND_PLACE;
      case 2 -> COINS_FOR_3RD_PLACE;
      default -> COINS_FOR_OTHERS;
    };
  }

  private int getCoinsForCourse(Course course) {
    return switch (course) {
      case FULL -> COINS_FOR_FULL;
      case HALF -> COINS_FOR_HALF;
    };
  }

  void addCoinIfEligible(User user, Problem problem) {
    if (!isEligibleForCoin(problem)) {
      return;
    }

    int coinToAdd = calculateCoinToAdd(problem, user);
    addWeeklyCoinsIfNeeded(user, coinToAdd);
  }

  int addCoinEligibleForTodayProblem(User user, Problem problem) {
    int coinToAdd = calculateCoinToAdd(problem, user);
    if (coinToAdd == 0) {
      return 0;
    }
    addCoinsToUser(user, coinToAdd, CoinType.DAILY, "오늘의 문제 해결");
    return coinToAdd;
  }

  private void addWeeklyCoinsIfNeeded(User user, int coinToAdd) {
    if (coinToAdd > 0) {
      addCoinsToUser(user, coinToAdd, CoinType.WEEKLY, "이번주 위클리 문제 해결");
    }
  }

  private void addCoinsToUser(User user, int coin, CoinType coinType, String description) {
    user.addCoin(coin);
    userService.saveUser(user);
    coinHistoryService.addCoinHistory(user, coin, coinType, description);
  }

  private boolean isEligibleForCoin(Problem problem) {
    return problem.getProblemType().getRoutine() == Routine.WEEKLY;
    // Weekly 문제는 항상 코인을 받음
  }

  private int calculateCoinToAdd(Problem problem, User user) {
    return problem.getProblemType().getRoutine().equals(Routine.DAILY)
        ? calculateCoinToAddForDaily(problem.getId())
        : calculateCoinToAddForWeekly(problem, user);
  }
}
