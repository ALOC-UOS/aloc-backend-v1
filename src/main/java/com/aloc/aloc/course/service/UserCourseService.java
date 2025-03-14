package com.aloc.aloc.course.service;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.CourseProblem;
import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.course.repository.UserCourseRepository;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserCourseProblem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import com.aloc.aloc.problem.service.UserCourseProblemService;
import com.aloc.aloc.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserCourseService {
  private final UserCourseRepository userCourseRepository;
  private final UserCourseProblemService userCourseProblemService;

  public List<UserCourse> getUserCoursesByUser(User user) {
    return userCourseRepository.findAllByUser(user);
  }

  public List<UserCourse> getUserCoursesInProcessByUser(User user) {
    return userCourseRepository.findAllByUserAndClosedAtAfter(user, LocalDateTime.now());
  }

  public boolean isEligibleToCreateUserCourse(User user) {
    return userCourseRepository.findAllByUser(user).size() < 3;
  }

  public UserCourse createUserCourse(User user, Course course) {
    UserCourse userCourse = UserCourse.of(user, course);
    userCourseRepository.save(userCourse);

    List<Problem> problems =
        course.getCourseProblemList().stream().map(CourseProblem::getProblem).toList();
    for (Problem problem : problems) {
      UserCourseProblem userCourseProblem =
          userCourseProblemService.createUserCourseProblem(userCourse, problem);
      if (userCourse.getUserCourseProblemList().isEmpty()) {
        userCourseProblem.updateUserCourseProblemStatus(UserCourseProblemStatus.UNSOLVED);
        userCourseProblemService.saveUserCourserProblem(userCourseProblem);
      }
      userCourse.addUserCourseProblem(userCourseProblem);
    }
    return userCourseRepository.save(userCourse);
  }

  public UserCourse getUserCourseByUserAndCourse(User user, Course course) {
    return userCourseRepository
        .findByUserAndCourse(user, course)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 코스입니다."));
  }

  public void closeUserCourse(UserCourse userCourse) {
    userCourseProblemService.closeUserCourseProblems(userCourse.getUserCourseProblemList());
    userCourse.updateUserCourseState(UserCourseState.FAILED);
  }
}
