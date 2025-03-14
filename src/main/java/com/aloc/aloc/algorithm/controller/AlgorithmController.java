package com.aloc.aloc.algorithm.controller;

import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/algorithm")
public class AlgorithmController {
  private final AlgorithmService algorithmService;

  @PostMapping("{name}")
  @Operation(summary = "알고리즘 생성", description = "알고리즘 이름으로 알고리즘 관련 정보를 스크랩핑하여 생성합니다.")
  public CustomApiResponse<String> createAlgorithm(
      @Parameter(description = "알고리즘 이름", required = true) @PathVariable String name) {
    return CustomApiResponse.onSuccess(algorithmService.createAlgorithm(name));
  }
}
