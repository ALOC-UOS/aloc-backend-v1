package com.aloc.aloc.problem.controller;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.facade.ProblemFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Problem API", description = "Problem API 입니다.")
@RequestMapping("/problems")
public class ProblemController {
  private final ProblemFacade problemFacade;

  @PatchMapping("/{problemId}")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(
      summary = "문제 해결 여부 체크",
      description = "사용자의 백준 ID로 해당 문제를 해결했는지 확인하고, 해결 시 보상 및 진행상태를 업데이트합니다.",
      responses = {
        @ApiResponse(responseCode = "200", description = "정상 처리됨"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (JWT 누락 또는 만료됨)"),
        @ApiResponse(responseCode = "404", description = "해당 문제 또는 사용자 정보를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이미 해결한 문제입니다"),
        @ApiResponse(responseCode = "422", description = "아직 채점이 완료되지 않았거나 문제를 해결하지 않았습니다")
      })
  public CustomApiResponse<ProblemSolvedResponseDto> checkProblemSolved(
      @PathVariable Integer problemId,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(
        problemFacade.checkProblemSolved(problemId, user.getUsername()));
  }
}
