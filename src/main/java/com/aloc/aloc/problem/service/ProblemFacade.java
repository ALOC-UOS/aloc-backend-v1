package com.aloc.aloc.problem.service;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.problem.dto.request.ProblemRequestDto;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.dto.response.TodayProblemSolvedResponseDto;
import com.aloc.aloc.problem.dto.response.UserProblemStatusResponse;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.enums.ProblemStatus;
import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.scraper.ProblemScrapingService;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ProblemFacade implements UserProblemRecordLoader {
  private final ProblemService problemService;
  private final UserService userService;
  private final ProblemMapper problemMapper;
  private final ProblemSolvingService problemSolvingService;
  private final ProblemScrapingService problemScrapingService;

  public UserProblemStatusResponse checkSolved(String username) {
    // 오늘의 문제와 다른 문제들의 풀이 여부를 한번에 확인합니다.
    User user = userService.getActiveUser(username);
    loadUserProblemRecord(user);
    return UserProblemStatusResponse.of(
        user,
        getTotalProblemCountByCourse(user.getCourse()) - user.getSolvedCount(),
        problemSolvingService.getTodayProblemSolved(user.getId(), user.getCourse()));
  }

  public TodayProblemSolvedResponseDto checkTodaySolved(String username) {
    // 오늘의 문제의 풀이 여부를 확인합니다.
    User user = userService.getActiveUser(username);
    return loadUserTodayProblemRecord(user);
  }

  public List<SolvedUserResponseDto> getSolvedUserListByProblemId(Long problemId) {
    problemService.checkProblemExist(problemId);
    return problemSolvingService.getSolvedUserListByProblemId(problemId).stream()
        .map(
            solvedProblem ->
                problemMapper.mapToSolvedUserResponseDto(solvedProblem.getUser(), solvedProblem))
        .collect(Collectors.toList());
  }

  public List<ProblemSolvedResponseDto> getWeeklyProblems(String username) {
    User user = userService.findUser(username);
    List<ProblemSolvedResponseDto> problems = problemSolvingService.getWeeklyProblems(user);
    return problems.stream()
        .sorted(
            Comparator.comparing(ProblemSolvedResponseDto::getIsSolved)
                .reversed()
                .thenComparing(ProblemSolvedResponseDto::getProblemDifficulty))
        .collect(Collectors.toList());
  }

  public List<ProblemSolvedResponseDto> getDailyProblems(String username) {
    User user = userService.findUser(username);
    return problemSolvingService.getDailyProblems(user);
  }

  public List<ProblemSolvedResponseDto> getUnsolvedProblemListByUser(
      String githubId, Integer season) {
    User user = userService.findUser(githubId);
    return problemSolvingService.getUnsolvedProblemListByUser(user, season);
  }

  public List<ProblemSolvedResponseDto> getSolvedProblemListByUser(
      String githubId, Integer season) {
    User user = userService.findUser(githubId);
    return problemSolvingService.getSolvedProblemListByUser(user, season);
  }

  public Boolean getTodayProblemSolved(Long userId, Course course) {
    return problemSolvingService.getTodayProblemSolved(userId, course);
  }

  public Integer getTotalProblemCountByCourse(Course course) {
    // 해당 코스의 공개 된 모든 문제 수를 가져옵니다.
    return problemService.getTotalProblemCount(course);
  }

  @Override
  public void loadUserProblemRecord(User user) {
    List<Problem> problems = problemService.getVisibleProblemsBySeasonAndCourse(user.getCourse());
    try {
      for (Problem problem : problems) {
        boolean isSolved = problemSolvingService.updateUserAndSaveSolvedProblem(user, problem);
        if (isSolved) {
          user.addSolvedCount();
          userService.checkUserRank(user);
          userService.saveUser(user);
        }
      }
    } catch (Exception e) {
      log.error("문제 풀이 정보를 업데이트하는 중 오류가 발생했습니다.", e);
      throw new RuntimeException("문제 풀이 정보를 업데이트하는 중 오류가 발생했습니다.");
    }
  }

  public TodayProblemSolvedResponseDto loadUserTodayProblemRecord(User user) {
    Problem todayProblem = problemService.findTodayProblemByCourse(user.getCourse());
    TodayProblemSolvedResponseDto response =
        problemSolvingService.updateTodaySolvedProblem(user, todayProblem);
    if (response.getSolvedStatus().equals(ProblemStatus.SOLVED)) {
      user.addSolvedCount();
      userService.checkUserRank(user);
      userService.saveUser(user);
    }
    return response;
  }

  public void updateAllUserProblem() {
    List<User> activeUsers = userService.getActiveUsers();
    for (User user : activeUsers) {
      problemSolvingService.addUserProblemRecord(user);
    }
  }

  public String addUserProblemForce(String username, String githubId, Long problemId) {
    userService.checkAdmin(username);
    User user = userService.findUser(githubId);
    Problem problem = problemService.findProblemById(problemId);
    problemSolvingService.addUserProblem(user, problem);
    return "success";
  }

  public String addProblemAndUserProblemForce(
      String username, ProblemRequestDto problemRequestDto) {
    userService.checkAdmin(username);
    Algorithm algorithm =
        problemService.getAlgorithmByAlgorithmName(problemRequestDto.getAlgorithm());
    ProblemType problemType = problemService.getProblemTypeById(problemRequestDto.getProblemType());
    if (problemService.checkProblemExistByProblemId(problemRequestDto.getProblemId())) {
      return "already exist";
    }
    Problem problem =
        problemScrapingService.getProblemByProblemId(
            problemRequestDto.getProblemId(), algorithm, problemType);
    List<User> activeUsers = userService.getActiveUsersByCourse(problemType.getCourse());
    for (User user : activeUsers) {
      problemSolvingService.addUserProblem(user, problem);
    }
    return "success";
  }
}
