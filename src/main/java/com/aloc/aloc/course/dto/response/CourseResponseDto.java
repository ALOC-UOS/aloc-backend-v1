package com.aloc.aloc.course.dto.response;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CourseResponseDto {
  @Schema(description = "코스아이디", example = "1")
  private Long courseId;

  @Schema(description = "코스 제목", example = "작심 삼일도 못한다고!?")
  private String title;

  @Schema(description = "코스 유형", example = "DAILY")
  private CourseType courseType;

  @Schema(description = "문제 수", example = "3")
  private int problemCnt;

  @Schema(description = "최소 랭크", example = "1")
  private int minRank;

  @Schema(description = "최대 랭크", example = "5")
  private int maxRank;

  @Schema(description = "평균 난이도", example = "3")
  private int averageRank;

  @Schema(description = "코스 생성수(인기순)", example = "10")
  private Long generateCnt;

  @Schema(description = "마감기한", example = "3")
  private int duration;

  @Schema(description = "생성일", example = "2024-03-04T19:37:55")
  private LocalDateTime createdAt;

  @Schema(description = "유저의 코스 성공 여부", example = "false")
  private boolean isSuccess;

  public static CourseResponseDto of(Course course, boolean isSuccess) {
    return CourseResponseDto.builder()
        .courseId(course.getId())
        .title(course.getTitle())
        .courseType(course.getCourseType())
        .problemCnt(course.getProblemCnt())
        .minRank(course.getMinRank())
        .maxRank(course.getMaxRank())
        .averageRank(course.getAverageRank())
        .generateCnt(course.getGenerateCnt())
        .createdAt(course.getCreatedAt())
        .isSuccess(isSuccess)
        .duration(course.getDuration())
        .build();
  }
}
