package com.aloc.aloc.problem.service.facade;

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
        userCourseProblemService.getUserCourseProblemByProblem(problem, user);
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
    List<CoinResponseDto> rewards = coinService.rewardUser(user, userCourseProblem);

    UserCourse userCourse = userCourseProblem.getUserCourse();
    Course course = userCourse.getCourse();

    List<UserCourseProblem> sorted = userCourseProblemService.getByUserCourse(userCourse);

    if (userCourseProblem.getProblemOrder().equals(course.getProblemCnt())) {
      userCourse.updateUserCourseState(UserCourseState.SUCCESS);
      course.addSuccessCnt();
    } else if (course.getCourseType() == CourseType.DEADLINE) {
      // 다음 문제 활성화를 위해, 1-based인 problemOrder 값을 0-based 리스트 인덱스로 그대로 사용합니다.
      // problemOrder=1 → sortedProblems.get(1) (problemOrder=2인 문제) 활성화
      activateNextProblem(sorted, userCourseProblem.getProblemOrder());
    }

    return rewards;
  }

  private void activateNextProblem(List<UserCourseProblem> sortedProblems, int nextIdx) {
    sortedProblems.get(nextIdx).updateUserCourseProblemStatus(UserCourseProblemStatus.UNSOLVED);
  }
}
