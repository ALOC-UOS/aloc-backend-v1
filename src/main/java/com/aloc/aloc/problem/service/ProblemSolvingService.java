package com.aloc.aloc.problem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemSolvingService {
  private final ProblemService problemService;
}
