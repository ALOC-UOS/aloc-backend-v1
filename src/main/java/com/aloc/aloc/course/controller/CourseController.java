package com.aloc.aloc.course.controller;

import com.aloc.aloc.course.dto.request.CourseRequestDto;
import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.user.facade.UserFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Course API", description = "course 관련 API입니다.")
public class CourseController {
  private final CourseService courseService;
  private final UserFacade userFacade;

  @Operation(
      summary = "코스 목록 조회",
      description =
          """
    모든 코스 목록을 페이징 형태로 조회합니다.
    로그인된 사용자는 각 코스에 대한 개인 진행 상태(`UserCourseState`) 정보도 함께 포함됩니다.
    미로그인 시에는 모든 코스의 상태가 `NOT_STARTED`로 제공됩니다.
    """)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "코스 목록 조회 성공"),
    @ApiResponse(responseCode = "401", description = "로그인 사용자 정보 없음"),
    @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @GetMapping("/courses")
  public CustomApiResponse<Page<CourseResponseDto>> getCourses(
      @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable,
      @RequestParam(required = false) CourseType courseType,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(
        (user != null)
            ? userFacade.getCoursesByUser(pageable, user.getUsername(), courseType)
            : courseService.getCourses(pageable, courseType));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/courses/{courseId}")
  @Operation(
      summary = "코스 정보 업데이트",
      description = "지정한 코스의 랭크 범위를 업데이트합니다. 관리자 권한이 필요합니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "코스 정보 업데이트 성공",
            content = @Content(schema = @Schema(implementation = CourseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 코스 ID"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
        @ApiResponse(responseCode = "404", description = "코스를 찾을 수 없음")
      })
  @SecurityRequirement(name = "JWT Auth")
  public CustomApiResponse<CourseResponseDto> updateCourse(@PathVariable Long courseId) {
    return CustomApiResponse.onSuccess(courseService.updateCourse(courseId));
  }

  @Operation(
      summary = "코스 생성",
      description =
          """
    새로운 코스를 생성합니다.

    - 코스 생성 후 알고리즘, 난이도 범위를 기반으로 문제를 자동으로 수집합니다.
    - 문제 수집 결과는 Discord Webhook을 통해 알림으로 전송됩니다.
    """)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "코스 생성 및 문제 스크래핑 성공"),
    @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음"),
    @ApiResponse(responseCode = "500", description = "서버 내부 오류 (스크래핑 실패 등)")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/course")
  @SecurityRequirement(name = "JWT Auth")
  public CustomApiResponse<CourseResponseDto> createCourse(
      @RequestBody @Valid CourseRequestDto courseRequestDto) throws IOException {
    return CustomApiResponse.onSuccess(courseService.createCourse(courseRequestDto));
  }
}
