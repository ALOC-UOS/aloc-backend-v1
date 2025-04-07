package com.aloc.aloc.usercourse.dto.response;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCourseProblemResponseDto {
  @Schema(description = "문제 아이디", example = "123")
  private int todayProblemId;

  @Schema(description = "문제 수", example = "7")
  private int problemCnt;

  @Schema(description = "문제 목록")
  private List<ProblemResponseDto> problems;

  public static UserCourseProblemResponseDto of(
      int todayProblemId, List<ProblemResponseDto> problems) {
    return UserCourseProblemResponseDto.builder()
        .todayProblemId(todayProblemId)
        .problemCnt(problems.size())
        .problems(problems)
        .build();
  }
}
