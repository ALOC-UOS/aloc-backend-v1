package com.aloc.aloc.usercourse.dto.response;

import com.aloc.aloc.course.dto.response.RankResponseDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.usercourse.entity.UserCourse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewUserCourseResponseDto {
  @Schema(description = "유저 코스 고유 아이디", example = "1L")
  private Long userCourseId;

  @Schema(description = "코스 이름", example = "작심삼일")
  private String title;

  @Schema(description = "코스 유형", example = "DAILY")
  private CourseType type;

  @Schema(description = "시작 날짜", example = "날짜")
  private LocalDateTime createdAt;

  @Schema(description = "마감 날짜", example = "날짜")
  private LocalDateTime closedAt;

  @Schema(description = "랭크")
  private RankResponseDto rank;

  public static NewUserCourseResponseDto of(UserCourse userCourse) {
    Course course = userCourse.getCourse();
    return NewUserCourseResponseDto.builder()
        .userCourseId(userCourse.getId())
        .title(course.getTitle())
        .type(course.getCourseType())
        .createdAt(userCourse.getCreatedAt())
        .closedAt(userCourse.getClosedAt())
        .rank(RankResponseDto.of(course.getMinRank(), course.getMaxRank(), course.getAverageRank()))
        .build();
  }
}
