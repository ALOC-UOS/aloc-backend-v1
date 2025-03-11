package com.aloc.aloc.user.dto.response;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public class UserCourseResponseDto {
  @Schema(description = "코스 고유 아이디", example = "1L")
  private Long id;

  @Schema(description = "코스 이름", example = "작심삼일")
  private String title;

  @Schema(description = "오늘의 문제 아이디", example = "1238")
  private int todayProblemId;

  @Schema(description = "마감 날짜", example = "날짜")
  private LocalDateTime closedAt;

  @Schema(description = "코스 문제 목록")
  private List<ProblemResponseDto> problems;

  public static UserCourseResponseDto of(
      UserCourse userCourse, Course course, List<ProblemResponseDto> problems, int todayProblemId) {
    return UserCourseResponseDto.builder()
        .id(course.getId())
        .title(course.getTitle())
        .todayProblemId(todayProblemId)
        .problems(problems)
        .closedAt(userCourse.getClosedAt())
        .build();
  }
}
