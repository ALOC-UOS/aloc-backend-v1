package com.aloc.aloc.common.fixture;

import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.dto.response.RankResponseDto;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import java.time.LocalDateTime;

public class TestFixture {

  public static CourseResponseDto getMockCourseResponseDtoByStatus(UserCourseState state) {
    return CourseResponseDto.builder()
        .id(1L)
        .title("코스1")
        .description("기초 설명")
        .type(CourseType.DAILY)
        .problemCnt(3)
        .rank(RankResponseDto.of(1, 3, 2))
        .generateCnt(10L)
        .duration(3)
        .createdAt(LocalDateTime.of(2024, 3, 4, 19, 37, 55))
        .status(state)
        .successCnt(23L)
        .build();
  }
}
