package com.aloc.aloc.algorithm.controller;

import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AlgorithmController {
  private final AlgorithmService algorithmService;

  @GetMapping("/algorithms")
  @Operation(summary = "모든 알고리즘 조회", description = "모든 알고리즘 목록을 조회합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "성공적으로 알고리즘 목록을 반환합니다.",
            content = @Content(schema = @Schema(implementation = AlgorithmResponseDto.class))),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public CustomApiResponse<List<AlgorithmResponseDto>> getAlgorithms() {
    return CustomApiResponse.onSuccess(algorithmService.getAlgorithms());
  }
}
