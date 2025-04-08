package com.aloc.aloc.algorithm.controller;

import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AlgorithmController {
  private final AlgorithmService algorithmService;

  @GetMapping("/algorithms")
  @Operation(summary = "모든 알고리즘 조회", description = "모든 알고리즘 목록을 조회합니다.")
  public CustomApiResponse<List<AlgorithmResponseDto>> getAlgorithms() {
    return CustomApiResponse.onSuccess(algorithmService.getAlgorithms());
  }
}
