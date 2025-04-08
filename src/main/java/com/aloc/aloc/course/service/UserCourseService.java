package com.aloc.aloc.course.service;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.CourseProblem;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.course.repository.UserCourseRepository;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import com.aloc.aloc.problem.service.UserCourseProblemService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.usercourse.entity.UserCourse;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserCourseService {
  private final UserCourseRepository userCourseRepository;
  private final UserCourseProblemService userCourseProblemService;

  public List<UserCourse> getUserCoursesByUser(User user) {
    return userCourseRepository.findAllByUser(user);
  }

  public List<UserCourse> getUserCoursesInProcessByUser(User user) {
    return userCourseRepository.findAllByUserAndUserCourseState(user, UserCourseState.IN_PROGRESS);
  }

  public List<UserCourse> getAllByUserAndUserCourseState(
      User user, UserCourseState userCourseState) {
    return userCourseRepository.findAllByUserAndUserCourseState(user, userCourseState);
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

  public UserCourse getUserCourseByUserAndCourseAndUserCourseState(
      User user, Course course, UserCourseState state) {
    return userCourseRepository
        .findByUserAndCourseAndUserCourseState(user, course, state)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 코스입니다."));
  }

  public void closeUserCourse(UserCourse userCourse) {
    userCourseProblemService.closeUserCourseProblems(userCourse.getUserCourseProblemList());
    userCourse.updateUserCourseState(UserCourseState.FAILED);
  }

  @Transactional
  public void closeFailUserCourse() {
    userCourseRepository
        .findAllByUserCourseStateAndClosedAtBefore(UserCourseState.IN_PROGRESS, LocalDateTime.now())
        .forEach(this::closeUserCourse);
  }

  @Transactional
  public void openDailyUserCourseProblem() {
    List<UserCourse> userCourses =
        userCourseRepository.findAllByUserCourseStateAndCourseType(
            UserCourseState.IN_PROGRESS, CourseType.DAILY);

    userCourses.forEach(
        userCourse -> {
          List<UserCourseProblem> sortedProblems =
              userCourse.getUserCourseProblemList().stream()
                  .sorted(
                      Comparator.comparing(
                          UserCourseProblem::getCreatedAt)) // createdAt 기준으로 오름차순 정렬
                  .toList();
          for (UserCourseProblem ucp : sortedProblems) {
            if (ucp.getUserCourseProblemStatus().equals(UserCourseProblemStatus.HIDDEN)) {
              ucp.updateUserCourseProblemStatus(UserCourseProblemStatus.UNSOLVED);
              break;
            } else {
              ucp.updateUserCourseProblemStatus(UserCourseProblemStatus.CLOSED);
            }
          }
        });
  }

  @Transactional
  public void deleteUserCourses(List<UserCourse> userCourses) {
    userCourseRepository.deleteAll(userCourses);
  }

  public UserCourse getUserCourseById(Long id) {
    return userCourseRepository
        .findById(id)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저코스 아이디 입니다."));
  }

  public int getClearRank(UserCourse userCourse) {
    return userCourseRepository.findClearRank(userCourse.getCourse(), userCourse.getUpdatedAt());
  }
}
