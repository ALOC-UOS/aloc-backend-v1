package com.aloc.aloc.course.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;

@Getter
public class CourseByProblemRequestDto {
  @Valid private CourseRequestDto courseRequestDto;

  @NotEmpty(message = "문제 번호 리스트는 비어있을 수 없습니다. ")
  private List<Integer> problemList;
}
