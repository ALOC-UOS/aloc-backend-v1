package com.aloc.aloc.course.dto.response;

import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.course.enums.UserCourseState;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public class UserCourseResponseDto {
  private Long id;
  private CourseResponseDto course;
  private UserCourseState userCourseState;
  private LocalDateTime closedAt;

  public static UserCourseResponseDto of(UserCourse userCourse) {
    return UserCourseResponseDto.builder()
        .id(userCourse.getId())
        .course(
            CourseResponseDto.of(
                userCourse.getCourse(), userCourse.getUserCourseState() == UserCourseState.SUCCESS))
        .userCourseState(userCourse.getUserCourseState())
        .closedAt(userCourse.getClosedAt())
        .build();
  }
}
