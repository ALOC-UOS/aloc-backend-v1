package com.aloc.aloc.admin.dto.response;

import com.aloc.aloc.course.dto.response.RankResponseDto;
import com.aloc.aloc.course.enums.CourseType;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminCourseResponseDto {
  private String courseName;
  private CourseType courseType;
  private RankResponseDto rank;
  private List<Integer> algorithmIdList;
  private Long generateCnt;

  public static AdminCourseResponseDto of(
      String courseName,
      CourseType courseType,
      RankResponseDto rank,
      List<Integer> algorithmIdList,
      Long generateCnt) {
    return AdminCourseResponseDto.builder()
        .courseName(courseName)
        .courseType(courseType)
        .rank(rank)
        .algorithmIdList(algorithmIdList)
        .generateCnt(generateCnt)
        .build();
  }
}
