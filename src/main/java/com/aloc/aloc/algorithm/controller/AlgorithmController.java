package com.aloc.aloc.algorithm.controller;

import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AlgorithmController {
  private final AlgorithmService algorithmService;

  @PostMapping("/algorithm/{name}")
  @Operation(summary = "알고리즘 생성", description = "알고리즘 이름으로 알고리즘 관련 정보를 스크랩핑하여 생성합니다.")
  public CustomApiResponse<String> createAlgorithm(
      @Parameter(description = "알고리즘 이름", required = true) @PathVariable String name) {
    return CustomApiResponse.onSuccess(algorithmService.createAlgorithm(name));
  }

  @GetMapping("/algorithms")
  @Operation(summary = "모든 알고리즘 조회", description = "모든 알고리즘 목록을 조회합니다.")
  public CustomApiResponse<List<AlgorithmResponseDto>> getAlgorithms() {
    return CustomApiResponse.onSuccess(algorithmService.getAlgorithms());
  }
}
