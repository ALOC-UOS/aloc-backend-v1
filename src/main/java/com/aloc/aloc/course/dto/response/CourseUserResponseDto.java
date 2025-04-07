package com.aloc.aloc.course.dto.response;

import com.aloc.aloc.usercourse.entity.UserCourse;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CourseUserResponseDto {
  private Long id;
  private CourseResponseDto course;
  private LocalDateTime closedAt;

  public static CourseUserResponseDto of(UserCourse userCourse) {
    return CourseUserResponseDto.builder()
        .id(userCourse.getId())
        .course(CourseResponseDto.of(userCourse.getCourse(), userCourse.getUserCourseState()))
        .closedAt(userCourse.getClosedAt())
        .build();
  }
}
