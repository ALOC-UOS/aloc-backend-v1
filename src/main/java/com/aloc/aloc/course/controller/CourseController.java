package com.aloc.aloc.course.controller;

import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.user.service.facade.UserFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/courses")
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
    @ApiResponse(
        responseCode = "200",
        description = "코스 목록 조회 성공",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseResponseDto.class))),
    @ApiResponse(responseCode = "401", description = "로그인 사용자 정보 없음"),
    @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @GetMapping()
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
}
