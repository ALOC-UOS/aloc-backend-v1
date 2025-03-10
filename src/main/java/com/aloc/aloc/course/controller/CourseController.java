package com.aloc.aloc.course.controller;

import com.aloc.aloc.course.dto.request.CourseRequestDto;
import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Course API", description = "course 관련 API입니다.")
public class CourseController {
  private final CourseService courseService;

  @GetMapping("/courses")
  @Operation(summary = "코스 목록 조회", description = "모든 코스 목록을 조회합니다.")
  public CustomApiResponse<Page<CourseResponseDto>> getCourses(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return CustomApiResponse.onSuccess(courseService.getCourses(pageable));
  }

  @PostMapping("/course")
  @Operation(summary = "코스 생성", description = "새로운 코스를 생성합니다.")
  public CustomApiResponse<CourseResponseDto> createCourse(
      @RequestBody CourseRequestDto courseRequestDto) throws IOException {
    return CustomApiResponse.onSuccess(courseService.createCourse(courseRequestDto));
  }
}
