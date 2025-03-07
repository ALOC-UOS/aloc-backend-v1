package com.aloc.aloc.problem.controller;

import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.problem.service.ProblemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Problem API", description = "Problem API 입니다.")
public class ProblemController {
  private final ProblemService problemService;
  private final ProblemFacade problemFacade;
}
