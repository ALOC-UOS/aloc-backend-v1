package com.aloc.aloc.user.dto.response;

import com.aloc.aloc.course.dto.response.RankResponseDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.course.enums.CourseType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCourseSimpleResponseDto {
  @Schema(description = "코스 고유 아이디", example = "1L")
  private Long id;

  @Schema(description = "코스 이름", example = "작심삼일")
  private String title;

  @Schema(description = "코스 유형", example = "DAILY")
  private CourseType courseType;

  @Schema(description = "시작 날짜", example = "날짜")
  private LocalDateTime createdAt;

  @Schema(description = "마감 날짜", example = "날짜")
  private LocalDateTime closedAt;

  @Schema(description = "랭크")
  private RankResponseDto rank;

  /**
   * Constructs a simplified DTO for a user's course from the given UserCourse entity.
   *
   * <p>This method extracts the associated course details from the provided UserCourse and creates a new
   * UserCourseSimpleResponseDto containing the course ID, title, type, created and closed dates, as well as
   * ranking information derived from the course’s minimum, maximum, and average rank values.
   *
   * @param userCourse the UserCourse entity from which to extract course data
   * @return a UserCourseSimpleResponseDto populated with course and ranking details
   */
  public static UserCourseSimpleResponseDto of(UserCourse userCourse) {
    Course course = userCourse.getCourse();

    return UserCourseSimpleResponseDto.builder()
        .id(userCourse.getId())
        .title(course.getTitle())
        .courseType(course.getCourseType())
        .createdAt(userCourse.getCreatedAt())
        .closedAt(userCourse.getClosedAt())
        .rank(RankResponseDto.of(course.getMinRank(), course.getMaxRank(), course.getAverageRank()))
        .build();
  }
}
