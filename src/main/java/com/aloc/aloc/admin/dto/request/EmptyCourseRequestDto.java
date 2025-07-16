package com.aloc.aloc.admin.dto.request;

import com.aloc.aloc.course.enums.CourseType;
import lombok.Data;

@Data
public class EmptyCourseRequestDto {
  private String title;
  private String description;
  private CourseType type;
  private int duration;
}
