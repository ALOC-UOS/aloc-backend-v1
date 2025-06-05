package com.aloc.aloc.problem.service;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import com.aloc.aloc.problem.repository.UserCourseProblemRepository;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.mapper.UserMapper;
import com.aloc.aloc.usercourse.entity.UserCourse;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserCourseProblemService {
  private final UserCourseProblemRepository userCourseProblemRepository;
  private final UserMapper userMapper;

  public Integer getTodayProblemId(List<UserCourseProblem> userCourseProblems) {
    for (int i = 0; i < userCourseProblems.size(); i++) {
      if (userCourseProblems
          .get(i)
          .getUserCourseProblemStatus()
          .equals(UserCourseProblemStatus.HIDDEN)) {
        return userCourseProblems.get(i - 1).getProblem().getProblemId();
      }
    }
    return userCourseProblems.get(userCourseProblems.size() - 1).getProblem().getProblemId();
  }

  public List<UserCourseProblem> getSolvedUserCourseProblemByProblem(Problem problem) {
    List<UserCourseProblem> userCourseProblems =
        userCourseProblemRepository.findAllByProblemAndUserCourseProblemStatusOrderBySolvedAtDesc(
            problem, UserCourseProblemStatus.SOLVED);

    if (userCourseProblems.isEmpty()) {
      return userCourseProblems;
    }
    Set<User> uniqueUsers = new HashSet<>();

    return userCourseProblems.stream()
        .filter(ucp -> uniqueUsers.add(ucp.getUserCourse().getUser())) // 중복 User 필터링
        .toList();
  }

  public UserCourseProblem createUserCourseProblem(
      UserCourse userCourse, Problem problem, int problemOrder) {
    UserCourseProblem userCourseProblem = UserCourseProblem.of(userCourse, problem, problemOrder);
    return userCourseProblemRepository.save(userCourseProblem);
  }

  public void saveUserCourserProblem(UserCourseProblem userCourseProblem) {
    userCourseProblemRepository.save(userCourseProblem);
  }

  public void closeUserCourseProblems(List<UserCourseProblem> userCourseProblems) {
    for (UserCourseProblem userCourseProblem : userCourseProblems) {
      if (isUnsolvedUserCourseProblem(userCourseProblem)) {
        userCourseProblem.updateUserCourseProblemStatus(UserCourseProblemStatus.CLOSED);
      }
    }
    userCourseProblemRepository.saveAll(userCourseProblems);
  }

  private static boolean isUnsolvedUserCourseProblem(UserCourseProblem userCourseProblem) {
    return userCourseProblem.getUserCourseProblemStatus() == UserCourseProblemStatus.HIDDEN
        || userCourseProblem.getUserCourseProblemStatus() == UserCourseProblemStatus.UNSOLVED;
  }

  public UserCourseProblem getUserCourseProblemByProblem(Problem problem, User user) {
    return userCourseProblemRepository
        .findByUserAndProblemAndStatus(user, problem, UserCourseProblemStatus.UNSOLVED)
        .orElseThrow(() -> new NoSuchElementException("이미 해결했거나 도전 중인 문제가 아닙니다."));
  }

  public List<ProblemResponseDto> mapToProblemResponseDto(List<UserCourseProblem> problems) {
    return problems.stream()
        .filter(ucp -> ucp.getUserCourseProblemStatus() != UserCourseProblemStatus.HIDDEN)
        .map(
            ucp -> {
              List<UserCourseProblem> userCourseProblems =
                  getSolvedUserCourseProblemByProblem(ucp.getProblem());
              LocalDateTime lastSolvedAt =
                  userCourseProblems.isEmpty() ? null : userCourseProblems.get(0).getSolvedAt();
              List<UserDetailResponseDto> solvingUserList =
                  userCourseProblems.stream()
                      .map(
                          userCourseProblem ->
                              userMapper.mapToUserDetailResponseDto(
                                  userCourseProblem.getUserCourse().getUser()))
                      .toList();
              return ProblemResponseDto.of(ucp, ucp.getProblem(), lastSolvedAt, solvingUserList);
            })
        .toList();
  }

  public List<UserCourseProblem> getByUserCourse(UserCourse userCourse) {
    return userCourseProblemRepository.findAllByUserCourseOrderByProblemOrderAsc(userCourse);
  }
}
