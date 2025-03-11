package com.aloc.aloc.problem.service;

import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserCourseProblem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import com.aloc.aloc.scraper.SolvedCheckingService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ProblemFacade {
  private final ProblemService problemService;
  private final UserService userService;
  private final UserCourseProblemService userCourseProblemService;
  private final SolvedCheckingService solvedCheckingService;

  @Transactional
  public String checkProblemSolved(Integer problemId, String oauthId) {
    User user = userService.getUser(oauthId);
    Problem problem =
        problemService
            .findProblemByProblemId(problemId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 문제 입니다."));
    UserCourseProblem userCourseProblem =
        userCourseProblemService.getUserCourseProblemByProblem(problem);
    UserCourse userCourse = userCourseProblem.getUserCourse();
    int userCourseIdx = userCourse.getUserCourseProblemList().indexOf(userCourseProblem);

    if (solvedCheckingService.isProblemSolved(user.getBaekjoonId(), problem, userCourseProblem)) {
      userCourseProblem.updateUserCourseProblemSolved();
      if (userCourseIdx == userCourse.getCourse().getDuration() - 1) {
        userCourse.updateUserCourseState(UserCourseState.SUCCESS);
      } else if (userCourse.getCourse().getCourseType().equals(CourseType.DEADLINE)) {
        userCourse
            .getUserCourseProblemList()
            .get(userCourseIdx + 1)
            .updateUserCourseProblemStatus(UserCourseProblemStatus.UNSOLVED);
      }
    }

    return null;
  }
}
