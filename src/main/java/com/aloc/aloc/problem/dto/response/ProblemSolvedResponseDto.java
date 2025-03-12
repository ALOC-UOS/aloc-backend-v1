package com.aloc.aloc.problem.dto.response;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProblemSolvedResponseDto {
  @Schema(description = "해결 여부", example = "true")
  private Boolean isSolved;

  @Schema(description = "코인 획득 리스트")
  private List<CoinResponseDto> coinResponseDtos;

  public static ProblemSolvedResponseDto success(List<CoinResponseDto> coinResponseDtos) {
    return ProblemSolvedResponseDto.builder()
        .isSolved(true)
        .coinResponseDtos(coinResponseDtos)
        .build();
  }

  public static ProblemSolvedResponseDto fail() {
    return ProblemSolvedResponseDto.builder().isSolved(false).coinResponseDtos(null).build();
  }
}
