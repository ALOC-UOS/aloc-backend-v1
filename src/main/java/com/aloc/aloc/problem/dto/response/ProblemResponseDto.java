package com.aloc.aloc.problem.dto.response;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserCourseProblem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
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

  @Schema(description = "유저 문제 해결 여부", example = "true")
  private Boolean isSolved;

  @Schema(description = "유저 문제 공개 여부", example = "false")
  private Boolean isHidden;

  @Schema(description = "해결한 유저들의 프로필 이미지", example = "uuid1, uuid2")
  private List<String> solvingUserProfileImageFileNames;

  public static ProblemResponseDto of(
      UserCourseProblem userCourseProblem,
      Problem problem,
      List<UserCourseProblem> userCourseProblems) {
    List<String> solvingUserProfileImageFileNames =
        userCourseProblems.subList(0, Math.min(userCourseProblems.size(), 3)).stream()
            .map(ucp -> ucp.getUserCourse().getUser().getProfileImageFileName())
            .toList();

    return ProblemResponseDto.builder()
        .problemId(problem.getProblemId())
        .title(problem.getTitle())
        .rank(problem.getRank())
        .isSolved(userCourseProblem.getUserCourseProblemStatus() == UserCourseProblemStatus.SOLVED)
        .isHidden(userCourseProblem.getUserCourseProblemStatus() == UserCourseProblemStatus.HIDDEN)
        .solvingUserNum(userCourseProblems.size())
        .lastSolvedAt(userCourseProblems.get(0).getSolvedAt())
        .solvingUserProfileImageFileNames(solvingUserProfileImageFileNames)
        .build();
  }
}
