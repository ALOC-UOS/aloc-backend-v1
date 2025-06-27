package com.aloc.aloc.admin.dto.response;

import com.aloc.aloc.course.enums.CourseType;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminCourseListResponseDto {
  private String courseName;
  private CourseType courseType;
  private Integer minRank;
  private Integer maxRank;
  private Integer averageRank;
  private List<Integer> algorithmIdList;
  private Long generateCnt;


  public static AdminCourseListResponseDto of(
      String courseName,
      CourseType courseType,
      Integer minRank,
      Integer maxRank,
      Integer averageRank,
      List<Integer> algorithmIdList,
      Long generateCnt) {
    return AdminCourseListResponseDto.builder()
        .courseName(courseName)
        .courseType(courseType)
        .minRank(minRank)
        .maxRank(maxRank)
        .averageRank(averageRank)
        .algorithmIdList(algorithmIdList)
        .generateCnt(generateCnt)
        .build();
  }
}
