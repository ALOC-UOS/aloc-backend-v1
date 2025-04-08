package com.aloc.aloc.course.dto.request;

import com.aloc.aloc.course.enums.CourseType;
import java.util.List;
import lombok.Getter;

@Getter
public class CourseRequestDto {
  private String title;
  private String description;
  private CourseType type;
  private int problemCnt;
  private int minRank;
  private int maxRank;
  private int duration;
  private List<Integer> algorithmIdList;
}
