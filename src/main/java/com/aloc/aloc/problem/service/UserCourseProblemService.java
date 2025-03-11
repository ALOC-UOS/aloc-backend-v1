package com.aloc.aloc.problem.service;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserCourseProblem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import com.aloc.aloc.problem.repository.UserCourseProblemRepository;
import com.aloc.aloc.user.entity.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserCourseProblemService {
  private final UserCourseProblemRepository userCourseProblemRepository;

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
    Set<User> uniqueUsers = new HashSet<>();

    return userCourseProblems.stream()
        .filter(ucp -> uniqueUsers.add(ucp.getUserCourse().getUser())) // 중복 User 필터링
        .toList();
  }
}
