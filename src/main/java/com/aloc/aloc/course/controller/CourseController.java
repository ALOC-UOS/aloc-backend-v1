package com.aloc.aloc.course.controller;

import com.aloc.aloc.course.dto.request.CourseRequestDto;
import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.dto.response.UserCourseResponseDto;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Course API", description = "course 관련 API입니다.")
public class CourseController {
  private final CourseService courseService;

  @GetMapping("/courses")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "코스 목록 조회", description = "모든 코스 목록을 조회합니다.")
  public CustomApiResponse<Page<CourseResponseDto>> getCourses(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable,
      @RequestParam(required = false) CourseType courseType,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(
        (user != null)
            ? courseService.getCoursesByUser(pageable, user.getUsername(), courseType)
            : courseService.getCourses(pageable, courseType));
  }

  @PostMapping("/course")
  @Operation(summary = "코스 생성", description = "새로운 코스를 생성합니다.")
  public CustomApiResponse<CourseResponseDto> createCourse(
      @RequestBody CourseRequestDto courseRequestDto) throws IOException {
    return CustomApiResponse.onSuccess(courseService.createCourse(courseRequestDto));
  }

  @PostMapping("/course/{courseId}")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "유저가 코스선택", description = "유저가 코스를 선택합니다.")
  public CustomApiResponse<UserCourseResponseDto> createUserCourse(
      @PathVariable Long courseId, @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(
        courseService.createUserCourse(courseId, user.getUsername()));
  }

  @PutMapping("/course/{courseId}")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "유저가 코스선택", description = "유저가 코스를 선택합니다.")
  public CustomApiResponse<UserCourseResponseDto> closeUserCourse(
      @PathVariable Long courseId, @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(courseService.closeUserCourse(courseId, user.getUsername()));
  }
}
