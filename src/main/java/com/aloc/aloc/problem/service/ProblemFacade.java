package com.aloc.aloc.problem.service;

import com.aloc.aloc.scraper.ProblemScrapingService;
import com.aloc.aloc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ProblemFacade {
  private final ProblemService problemService;
  private final UserService userService;
  private final ProblemSolvingService problemSolvingService;
  private final ProblemScrapingService problemScrapingService;
}
