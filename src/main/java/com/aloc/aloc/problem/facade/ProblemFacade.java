package com.aloc.aloc.problem.facade;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.enums.CoinType;
import com.aloc.aloc.coin.service.CoinService;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.global.apipayload.exception.AlreadySolvedProblemException;
import com.aloc.aloc.global.apipayload.exception.ProblemNotYetSolvedException;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problem.service.UserCourseProblemService;
import com.aloc.aloc.scraper.SolvedCheckingService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import com.aloc.aloc.usercourse.entity.UserCourse;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import java.util.ArrayList;
import java.util.Comparator;
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

  @Transactional
  public ProblemSolvedResponseDto checkProblemSolved(Integer problemId, String oauthId) {
    User user = userService.getUser(oauthId);
    Problem problem = problemService.getProblemByProblemId(problemId);
    UserCourseProblem userCourseProblem =
        userCourseProblemService.getUserCourseProblemByProblem(problem);
    if (userCourseProblem.getUserCourseProblemStatus().equals(UserCourseProblemStatus.SOLVED)) {
      throw new AlreadySolvedProblemException("이미 해결한 문제입니다.");
    }

    if (!solvedCheckingService.isProblemSolved(user.getBaekjoonId(), problem, userCourseProblem)) {
      throw new ProblemNotYetSolvedException("아직 문제의 채점이 완료되지 않았거나 해결되지 않았습니다.");
    }
    userService.updateUserBaekjoonRank(user);
    userCourseProblem.updateUserCourseProblemSolved();
    user.updateUserBySolvingProblem();

    List<CoinResponseDto> coinResponseDtos = handleSolvedProblem(user, userCourseProblem);
    boolean isCourseDone =
        coinResponseDtos.stream().anyMatch(cr -> cr.getType().equals(CoinType.COURSE_REWARD));

    return ProblemSolvedResponseDto.success(isCourseDone, coinResponseDtos);
  }

  private List<CoinResponseDto> handleSolvedProblem(
      User user, UserCourseProblem userCourseProblem) {
    List<CoinResponseDto> coinResponseDtos = new ArrayList<>();

    // 기본 코인 지급
    coinResponseDtos.add(coinService.giveCoinBySolvingProblem(user));
    checkAndGiveStreakCoin(user, coinResponseDtos);

    UserCourse userCourse = userCourseProblem.getUserCourse();
    Course course = userCourse.getCourse();
    // createdAt 오름차순으로 정렬된 리스트 생성
    List<UserCourseProblem> sortedList =
        userCourse.getUserCourseProblemList().stream()
            .sorted(Comparator.comparing(UserCourseProblem::getCreatedAt))
            .toList();

    // 정렬된 리스트에서 인덱스 구하기
    int problemIndex = sortedList.indexOf(userCourseProblem);

    if (isCourseCompleted(problemIndex, course)) {
      userCourse.updateUserCourseState(UserCourseState.SUCCESS);
      course.addSuccessCnt();
      coinResponseDtos.add(coinService.giveCoinBySolvingCourse(user, course));
    } else if (course.getCourseType() == CourseType.DEADLINE) {
      activateNextProblem(userCourse, problemIndex);
    }

    return coinResponseDtos;
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
