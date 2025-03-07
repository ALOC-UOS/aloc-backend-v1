package com.aloc.aloc.problem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemResponseDto {
  @Null
  @Schema(description = "문제 고유 ID", example = "749")
  private Long id;

  @Schema(description = "문제 백준 ID", example = "1080")
  private Integer problemId;

  @Schema(description = "문제 제목", example = "A와 B ")
  private String title;

  @Schema(description = "문제 난이도", example = "8")
  private Integer difficulty;

  @Schema(description = "푼 사람 수", example = "10")
  private Integer solvingCount;
}
