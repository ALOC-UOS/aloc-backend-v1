package com.aloc.aloc.coin.controller;

import com.aloc.aloc.coin.service.CoinService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CoinController {
  private final CoinService coinService;

  @PatchMapping("/coin/{coinType}")
  @Operation(
      summary = "코인 획득",
      description = "코인 타입이 1 이면 문제 해결, 코인 타입이 2 이면 문제 해결과 연속 7일 문제 해결로 코인을 무조건 획득하는 테스트용 api 입니다.")
  public CustomApiResponse<ProblemSolvedResponseDto> createAlgorithm(
      @Parameter(description = "알고리즘 이름", required = true) @PathVariable int coinType,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(
        coinService.giveCoinByCoinType(user.getUsername(), coinType));
  }
}
