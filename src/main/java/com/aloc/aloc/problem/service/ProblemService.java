package com.aloc.aloc.problem.service;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import java.util.NoSuchElementException;
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

  public Problem saveProblem(Problem problem) {
    problemRepository.save(problem);
    return problem;
  }

  public Problem getProblemByProblemId(Integer problemId) {
    return findProblemByProblemId(problemId)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 문제 입니다."));
  }
}
