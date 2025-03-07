package com.aloc.aloc.problem.service;

import com.aloc.aloc.problem.repository.UserProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProblemService {
  private final UserProblemRepository userProblemRepository;
}
