package com.aloc.aloc.course.dto.response;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CourseResponseDto {
  @Schema(description = "코스아이디", example = "1")
  private Long id;

  @Schema(description = "코스 제목", example = "작심 삼일도 못한다고!?")
  private String title;

  @Schema(description = "코스 유형", example = "DAILY")
  private CourseType type;

  @Schema(description = "문제 수", example = "3")
  private int problemCnt;

  @Schema(description = "랭크")
  private RankResponseDto rank;

  @Schema(description = "코스 생성수(인기순)", example = "10")
  private Long generateCnt;

  @Schema(description = "마감기한", example = "3")
  private int duration;

  @Schema(description = "생성일", example = "2024-03-04T19:37:55")
  private LocalDateTime createdAt;

  @Schema(description = "유저의 코스 성공 여부", example = "SUCCESS")
  private UserCourseState state;

  @Schema(description = "다른 유저의 성공 횟수", example = "23")
  private Long successCnt;

  public static CourseResponseDto of(Course course, UserCourseState userCourseState) {
    return CourseResponseDto.builder()
        .id(course.getId())
        .title(course.getTitle())
        .type(course.getCourseType())
        .problemCnt(course.getProblemCnt())
        .rank(RankResponseDto.of(course.getMinRank(), course.getMaxRank(), course.getAverageRank()))
        .generateCnt(course.getGenerateCnt())
        .createdAt(course.getCreatedAt())
        .state(userCourseState == null ? UserCourseState.NOT_STARTED : userCourseState)
        .duration(course.getDuration())
        .successCnt(0L)
        .build();
  }
}
