package com.aloc.aloc.problem.service;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemService {
  private final ProblemRepository problemRepository;

  public Optional<Problem> findProblemByProblemId(Integer problemId) {
    return problemRepository.findByProblemId(problemId);
  }

  public void checkProblemExist(Long id) {
    Optional<Problem> problem = problemRepository.findById(id);
    if (problem.isEmpty()) {
      throw new IllegalArgumentException("해당 문제가 존재하지 않습니다.");
    }
  }

  public Problem saveProblem(Problem problem) {
    problemRepository.save(problem);
    return problem;
  }
}
