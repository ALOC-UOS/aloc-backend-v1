package com.aloc.aloc.problem.controller;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.service.ProblemFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Problem API", description = "Problem API 입니다.")
public class ProblemController {
  private final ProblemFacade problemFacade;

  @PutMapping("/problem/{problemId}")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "문제 해결", description = "문제 해결 여부를 확인합니다.")
  public CustomApiResponse<ProblemSolvedResponseDto> checkProblemSolved(
      @PathVariable Integer problemId,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(
        problemFacade.checkProblemSolved(problemId, user.getUsername()));
  }
}
