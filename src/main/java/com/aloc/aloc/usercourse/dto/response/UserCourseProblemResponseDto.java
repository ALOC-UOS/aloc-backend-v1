package com.aloc.aloc.usercourse.dto.response;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCourseProblemResponseDto {
  private int todayProblemId;
  private int problemCnt;
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
