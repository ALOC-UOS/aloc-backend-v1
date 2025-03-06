package com.aloc.aloc.problem.dto.response;

import com.aloc.aloc.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserProblemStatusResponse {
  @Schema(description = "유저 이름", example = "홍길동")
  private String username;

  @Schema(description = "깃허브 아이디", example = "githubId")
  private String githubId;

  @Schema(description = "백준 아이디", example = "baekjoonId")
  private String baekjoonId;

  @Schema(description = "해결한 문제 수", example = "4")
  private Integer solvedCount;

  @Schema(description = "해결하지 못한 문제 수", example = "2")
  private Integer unsolvedCount;

  @Schema(description = "오늘의 문제 해결 여부", example = "true")
  private Boolean todaySolved;

  public static UserProblemStatusResponse of(
      User user, Integer unsolvedCount, Boolean todaySolved) {
    return UserProblemStatusResponse.builder()
        .username(user.getName())
        .solvedCount(user.getSolvedCount())
        .unsolvedCount(unsolvedCount)
        .todaySolved(todaySolved)
        .build();
  }
}
