package com.aloc.aloc.problem.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.enums.UserProblemStatus;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.UserProblemRepository;
import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CoinServiceTest {
  @Mock private UserProblemRepository userProblemRepository;
  @Mock private AlgorithmService algorithmService;
  @Mock private ProblemRepository problemRepository;
  @InjectMocks private CoinService coinService;

  private static final int CURRENT_SEASON = 2;
  private static final int COINS_FOR_1ST_PLACE = 50;
  private static final int COINS_FOR_2ND_PLACE = 40;
  private static final int COINS_FOR_3RD_PLACE = 30;
  private static final int COINS_FOR_OTHERS = 20;
  private static final int COINS_FOR_FULL = 200;
  private static final int COINS_FOR_HALF = 100;
  private final Long problemId = 1L;
  private Algorithm algorithmOfThisWeek;
  private ProblemType fullWeekly;
  private ProblemType halfWeekly;
  private Problem problemFullWeekly;
  private Problem problemHalfWeekly;
  private Problem problemFullWeeklyNotThisWeek;
  private User user;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(coinService, "currentSeason", CURRENT_SEASON);
    Algorithm algorithmOfProblem = new Algorithm(1, 1, "알고리즘 1", CURRENT_SEASON, false);
    algorithmOfThisWeek = new Algorithm(2, 2, "알고리즘 2", CURRENT_SEASON, false);
    fullWeekly = new ProblemType(3L, Routine.WEEKLY, Course.FULL);
    halfWeekly = new ProblemType(4L, Routine.WEEKLY, Course.HALF);
    problemFullWeekly =
        new Problem(1L, "풀 위클리", 10, algorithmOfThisWeek, false, 1, fullWeekly, null);
    problemHalfWeekly =
        new Problem(2L, "하프 위클리", 11, algorithmOfThisWeek, false, 2, halfWeekly, null);
    problemFullWeeklyNotThisWeek =
        new Problem(3L, "풀 위클리", 10, algorithmOfProblem, false, 3, fullWeekly, null);
    user =
        new User(
            "홍길동",
            "baekjoonId",
            "githubId",
            "2021920000",
            "password",
            "discordId",
            10,
            "notionEmail",
            Course.FULL,
            "oauthId",
            "userId",
            "nickname",
            "profileImageUrl");
  }

  @Test
  @DisplayName("daily 문제 1등으로 풀었을 때 테스트")
  void calculateCoinToAddForDailyFirstPlace() {
    when(userProblemRepository.countSolvingUsersByProblemId(
            problemId, CURRENT_SEASON, UserProblemStatus.SOLVED))
        .thenReturn(0);

    int coins = coinService.calculateCoinToAddForDaily(problemId);

    assertEquals(COINS_FOR_1ST_PLACE, coins);
  }

  @Test
  @DisplayName("daily 문제 2등으로 풀었을 때 테스트")
  void calculateCoinToAddForDailySecondPlace() {
    when(userProblemRepository.countSolvingUsersByProblemId(
            problemId, CURRENT_SEASON, UserProblemStatus.SOLVED))
        .thenReturn(1);

    int coins = coinService.calculateCoinToAddForDaily(problemId);

    assertEquals(COINS_FOR_2ND_PLACE, coins);
  }

  @Test
  @DisplayName("daily 문제 3등으로 풀었을 때 테스트")
  void calculateCoinToAddForDailyThirdPlace() {
    when(userProblemRepository.countSolvingUsersByProblemId(
            problemId, CURRENT_SEASON, UserProblemStatus.SOLVED))
        .thenReturn(2);

    int coins = coinService.calculateCoinToAddForDaily(problemId);

    assertEquals(COINS_FOR_3RD_PLACE, coins);
  }

  @Test
  @DisplayName("daily 문제 4등 이하로 풀었을 때 테스트")
  void calculateCoinToAddForDailyOthers() {
    when(userProblemRepository.countSolvingUsersByProblemId(
            problemId, CURRENT_SEASON, UserProblemStatus.SOLVED))
        .thenReturn(3);

    int coins = coinService.calculateCoinToAddForDaily(problemId);

    assertEquals(COINS_FOR_OTHERS, coins);
  }

  @Test
  @DisplayName("이번주에 FULL 코스 유저가 이번주 weekly 문제 다 풀었을 때 테스트")
  void calculateCoinToAddForWeeklyFullCourse() {
    when(algorithmService.getWeeklyAlgorithmBySeason(CURRENT_SEASON))
        .thenReturn(Optional.of(algorithmOfThisWeek));
    when(problemRepository.findAllByAlgorithmAndProblemType(algorithmOfThisWeek, fullWeekly))
        .thenReturn(List.of(new Problem()));
    when(userProblemRepository.countByUnsolvedProblemsIn(
            any(List.class), eq(user.getId()), eq(UserProblemStatus.UNSOLVED)))
        .thenReturn(0);

    int coins = coinService.calculateCoinToAddForWeekly(problemFullWeekly, user);

    assertEquals(COINS_FOR_FULL, coins);
  }

  @Test
  @DisplayName("이번주에 HALF 코스 유저가 이번주 weekly 문제 다 풀었을 때 테스트")
  void calculateCoinToAddForWeeklyHalfCourse() {
    when(algorithmService.getWeeklyAlgorithmBySeason(CURRENT_SEASON))
        .thenReturn(Optional.of(algorithmOfThisWeek));
    when(problemRepository.findAllByAlgorithmAndProblemType(algorithmOfThisWeek, halfWeekly))
        .thenReturn(List.of(new Problem()));
    when(userProblemRepository.countByUnsolvedProblemsIn(
            any(List.class), eq(user.getId()), eq(UserProblemStatus.UNSOLVED)))
        .thenReturn(0);

    int coins = coinService.calculateCoinToAddForWeekly(problemHalfWeekly, user);

    assertEquals(COINS_FOR_HALF, coins);
  }

  @Test
  @DisplayName("해당 weekly 문제의 알고리즘이 이번주 알고리즘이 아닐 때 테스트")
  void calculateCoinToAddForWeeklyNoMatchingAlgorithm() {
    when(algorithmService.getWeeklyAlgorithmBySeason(CURRENT_SEASON))
        .thenReturn(Optional.of(algorithmOfThisWeek));

    int coins = coinService.calculateCoinToAddForWeekly(problemFullWeeklyNotThisWeek, user);

    assertEquals(0, coins);
  }

  @Test
  @DisplayName("이번주 weekly 문제를 다 풀지 않았을 때")
  void calculateCoinToAddForWeeklySomeUnsolvedProblems() {
    when(algorithmService.getWeeklyAlgorithmBySeason(CURRENT_SEASON))
        .thenReturn(Optional.of(algorithmOfThisWeek));
    when(problemRepository.findAllByAlgorithmAndProblemType(algorithmOfThisWeek, fullWeekly))
        .thenReturn(List.of(new Problem()));
    when(userProblemRepository.countByUnsolvedProblemsIn(
            any(List.class), eq(user.getId()), eq(UserProblemStatus.UNSOLVED)))
        .thenReturn(1);

    int coins = coinService.calculateCoinToAddForWeekly(problemFullWeekly, user);

    assertEquals(0, coins);
  }
}
