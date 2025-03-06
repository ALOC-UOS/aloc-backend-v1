package com.aloc.aloc.algorithm.controller;

import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/algorithm")
@Tag(name = "Algorithm API", description = "Algorithm API 입니다.")
public class AlgorithmController {

  private final AlgorithmService algorithmService;

  @GetMapping()
  @Operation(summary = "알고리즘 목록 조회", description = "모든 공개된 알고리즘 목록을 최신 순서로 조회합니다.")
  public CustomApiResponse<List<AlgorithmResponseDto>> getAlgorithms() {
    return CustomApiResponse.onSuccess(algorithmService.getAlgorithms());
  }

  @GetMapping("/{season}")
  @Operation(summary = "특정 시즌 알고리즘 목록 조회", description = "특정 시즌의 공개된 알고리즘 목록을 최신 순서로 조회합니다.")
  public CustomApiResponse<AlgorithmResponseDto> getAlgorithmsBySeason(
      @Parameter(description = "알고리즘 시즌", required = true) @PathVariable() int season) {
    return CustomApiResponse.onSuccess(algorithmService.getAlgorithmsBySeason(season));
  }
}
