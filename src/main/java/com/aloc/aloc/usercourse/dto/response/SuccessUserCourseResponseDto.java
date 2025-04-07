package com.aloc.aloc.usercourse.dto.response;

import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuccessUserCourseResponseDto {
  @Schema(description = "코스 이름", example = "작심삼일")
  private String title;

  @Schema(description = "코스 유형", example = "DAILY")
  private CourseType type;

  @Schema(description = "코스 해결 순위", example = "3")
  private int clearRank;

  @Schema(description = "추가된 코인", example = "140")
  private int addedCoin;

  @Schema(description = "추천 코스")
  private List<CourseResponseDto> recommendedCourses;

  public static SuccessUserCourseResponseDto of(
      Course course, int clearRank, int addedCoin, List<CourseResponseDto> courses) {
    return SuccessUserCourseResponseDto.builder()
        .title(course.getTitle())
        .type(course.getCourseType())
        .clearRank(clearRank)
        .addedCoin(addedCoin)
        .recommendedCourses(courses)
        .build();
  }
}
