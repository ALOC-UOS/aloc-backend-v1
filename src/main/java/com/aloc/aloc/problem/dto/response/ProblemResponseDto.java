package com.aloc.aloc.problem.dto.response;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserCourseProblem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
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
  @Schema(description = "문제 백준 ID", example = "1080")
  private Integer problemId;

  @Schema(description = "문제 제목", example = "A와 B ")
  private String title;

  @Schema(description = "문제 난이도", example = "8")
  private Integer rank;

  @Schema(description = "푼 사람 수", example = "10")
  private Integer solvingUserNum;

  @Schema(description = "가장 최근 해결한 시간", example = "시간")
  private LocalDateTime lastSolvedAt;

  @Schema(description = "문제 상태", example = "IN_PROGRESS")
  private UserCourseProblemStatus status;

  @Schema(description = "해결한 유저들의 프로필 이미지", example = "uuid1, uuid2")
  private List<UserDetailResponseDto> solvingUserList;

  public static ProblemResponseDto of(
      UserCourseProblem userCourseProblem,
      Problem problem,
      LocalDateTime lastSolvedAt,
      List<UserDetailResponseDto> solvingUserList) {

    // 문제를 해결한 유저 수
    int solvedUserNum = solvingUserList.size();

    return ProblemResponseDto.builder()
        .problemId(problem.getProblemId())
        .title(problem.getTitle())
        .rank(problem.getRank())
        .status(userCourseProblem.getUserCourseProblemStatus())
        .solvingUserNum(solvedUserNum)
        .lastSolvedAt(lastSolvedAt)
        .solvingUserList(solvingUserList)
        .build();
  }
}
