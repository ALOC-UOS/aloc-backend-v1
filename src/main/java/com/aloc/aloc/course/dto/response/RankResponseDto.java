package com.aloc.aloc.course.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RankResponseDto {
  @Schema(description = "최소 랭크", example = "1")
  private int min;

  @Schema(description = "최대 랭크", example = "5")
  private int max;

  @Schema(description = "평균 난이도", example = "3")
  private int avg;

  public static RankResponseDto of(int minRank, int maxRank, int averageRank) {
    return RankResponseDto.builder().min(minRank).max(maxRank).avg(averageRank).build();
  }
}
