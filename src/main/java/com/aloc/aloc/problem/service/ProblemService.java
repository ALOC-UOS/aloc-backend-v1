package com.aloc.aloc.problem.service;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.user.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemService {
  private final ProblemRepository problemRepository;
  private final UserService userService;
  private final UserCourseProblemService userCourseProblemService;

  public Optional<Problem> findProblemByProblemId(Integer problemId) {
    return problemRepository.findByProblemId(problemId);
  }

  public Problem saveProblem(Problem problem) {
    problemRepository.save(problem);
    return problem;
  }
}
