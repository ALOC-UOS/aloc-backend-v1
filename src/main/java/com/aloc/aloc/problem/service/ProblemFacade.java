package com.aloc.aloc.problem.service;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.service.CoinService;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserCourseProblem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.scraper.SolvedCheckingService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemFacade {

  private final ProblemService problemService;
  private final UserService userService;
  private final UserCourseProblemService userCourseProblemService;
  private final SolvedCheckingService solvedCheckingService;
  private final CoinService coinService;
  private final BaekjoonRankScrapingService baekjoonRankScrapingService;

  @Transactional
  public ProblemSolvedResponseDto checkProblemSolved(Integer problemId, String oauthId) {
    User user = userService.getUser(oauthId);
    Problem problem = problemService.getProblemByProblemId(problemId);
    UserCourseProblem userCourseProblem =
        userCourseProblemService.getUserCourseProblemByProblem(problem);
    UserCourse userCourse = userCourseProblem.getUserCourse();
    Course course = userCourse.getCourse();
    int userCourseIdx = userCourse.getUserCourseProblemList().indexOf(userCourseProblem);
    List<CoinResponseDto> coinResponseDtos = new ArrayList<>();

    if (!solvedCheckingService.isProblemSolved(user.getBaekjoonId(), problem, userCourseProblem)) {
      return ProblemSolvedResponseDto.fail();
    }

    user.setRank(baekjoonRankScrapingService.extractBaekjoonRank(user.getBaekjoonId()));

    // 문제 해결 성공 시 처리
    userCourseProblem.updateUserCourseProblemSolved();
    coinResponseDtos.add(coinService.giveCoinBySolvingProblem(user));
    user.updateUserBySolvingProblem();
    checkAndGiveStreakCoin(user, coinResponseDtos);

    if (isCourseCompleted(userCourseIdx, course)) {
      userCourse.updateUserCourseState(UserCourseState.SUCCESS);
      coinResponseDtos.add(coinService.giveCoinBySolvingCourse(user, course));
    } else if (course.getCourseType().equals(CourseType.DEADLINE)) {
      activateNextProblem(userCourse, userCourseIdx);
    }

    return ProblemSolvedResponseDto.success(coinResponseDtos);
  }

  private boolean isCourseCompleted(int userCourseIdx, Course course) {
    return userCourseIdx == course.getProblemCnt() - 1;
  }

  private void activateNextProblem(UserCourse userCourse, int currentIndex) {
    userCourse
        .getUserCourseProblemList()
        .get(currentIndex + 1)
        .updateUserCourseProblemStatus(UserCourseProblemStatus.UNSOLVED);
  }

  private void checkAndGiveStreakCoin(User user, List<CoinResponseDto> coinResponseDtos) {
    if (user.getConsecutiveSolvedDays() % 7 == 0) {
      coinResponseDtos.add(coinService.giveCoinByStreakDays(user));
    }
  }
}
