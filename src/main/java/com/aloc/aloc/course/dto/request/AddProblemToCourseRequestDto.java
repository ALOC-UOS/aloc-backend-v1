package com.aloc.aloc.course.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddProblemToCourseRequestDto {
  @NotNull private Long courseId;

  @NotNull private Integer problemId;
}
