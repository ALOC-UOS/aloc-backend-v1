package com.aloc.aloc.course.dto.response;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CourseResponseDto {
  private Long courseId;
  private String title;
  private CourseType courseType;
  private int problemCnt;
  private int minRank;
  private int maxRank;
  private int averageRank;
  private Long generateCnt;
  private int duration;
  private LocalDateTime createdAt;
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
