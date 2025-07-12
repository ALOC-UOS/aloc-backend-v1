package com.aloc.aloc.admin.controller;

import com.aloc.aloc.admin.dto.request.AdminCoinTransactionRequestDto;
import com.aloc.aloc.admin.dto.request.AdminRoleChangeRequestDto;
import com.aloc.aloc.admin.dto.response.AdminCourseResponseDto;
import com.aloc.aloc.admin.dto.response.AdminDashboardResponseDto;
import com.aloc.aloc.admin.dto.response.AdminWithdrawResponseDto;
import com.aloc.aloc.admin.service.AdminService;
import com.aloc.aloc.course.dto.request.AddProblemToCourseRequestDto;
import com.aloc.aloc.course.dto.request.CourseRequestDto;
import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.global.apipayload.status.SuccessStatus;
import com.aloc.aloc.scraper.ProblemScrapingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
  private final AdminService adminService;
  private final CourseService courseService;
  private final ProblemScrapingService problemScrapingService;

  @GetMapping("/dashboard")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(
      summary = "어드민 대시보드 통계 조회",
      description = "관리자 메인 페이지에서 필요한 전체 사용자 수, 코스 수 등의 통계 정보를 조회합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "성공적으로 대시보드 통계 정보를 반환합니다.",
            content = @Content(schema = @Schema(implementation = AdminDashboardResponseDto.class))),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않았거나 관리자 권한이 없는 경우",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public CustomApiResponse<AdminDashboardResponseDto> getDashboard(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(adminService.getDashboard(user.getUsername()));
  }

  @GetMapping("/courses")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "어드민 코스 목록 조회", description = "관리자 코스 목록 페이지에서 코스 목록을 조회합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "성공적으로 코스 목록을 반환합니다.",
            content =
                @Content(
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = AdminCourseResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않았거나 관리자 권한이 없는 경우",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public CustomApiResponse<List<AdminCourseResponseDto>> getCourseList(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(adminService.getCourseList(user.getUsername()));
  }

  @PatchMapping("/users/role")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "어드민 유저 권한 변경", description = "관리자 유저 목록 페이지에서 여러명의 유저의 권한을 업데이트 합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 권한이 변경되었습니다."),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않았거나 관리자 권한이 없는 경우",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public CustomApiResponse<String> updateRole(
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @RequestBody @Valid AdminRoleChangeRequestDto adminRoleChangeRequestDto) {
    return CustomApiResponse.onSuccess(
        adminService.updateUserRole(user.getUsername(), adminRoleChangeRequestDto));
  }

  @PatchMapping("/coin-transactions")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(
      summary = "코인 지급 및 회수",
      description = "관리자 코인 지급 및 회수 페이지에서 선택한 유저들에게 코인을 지급하거나 차감합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "성공적으로 코인을 지급하거나 차감합니다.",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않았거나 관리자 권한이 없는 경우",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public CustomApiResponse<String> postCoinTransactions(
      @RequestBody @Valid AdminCoinTransactionRequestDto requestDto,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(
        adminService.processCoinTransactions(user.getUsername(), requestDto));
  }

  @DeleteMapping("admin/withdraw")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "유저 추방", description = "관리자가 유저를 추방합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "성공적으로 유저를 방출하였습니다.",
            content = @Content(schema = @Schema(implementation = AdminWithdrawResponseDto.class))),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않았거나 관리자 권한이 없는 경우",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public CustomApiResponse<AdminWithdrawResponseDto> withdrawByAdmin(
      @Parameter(hidden = true) @AuthenticationPrincipal User user, @RequestParam UUID uuid) {
    return CustomApiResponse.onSuccess(adminService.killUser(user.getUsername(), uuid));
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
    @ApiResponse(
        responseCode = "201",
        description = "코스 생성 및 문제 스크래핑 성공",
        content = @Content(schema = @Schema(implementation = CourseResponseDto.class))),
    @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음"),
    @ApiResponse(responseCode = "500", description = "서버 내부 오류 (스크래핑 실패 등)")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/course")
  @SecurityRequirement(name = "JWT Auth")
  public CustomApiResponse<CourseResponseDto> createCourse(
      @RequestBody @Valid CourseRequestDto courseRequestDto,
      @Parameter(hidden = true) @AuthenticationPrincipal User user)
      throws IOException {

    return CustomApiResponse.of(
        SuccessStatus._CREATED, courseService.createCourse(user.getUsername(), courseRequestDto));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "빈 코스 생성", description = "비어있는 코스를 생성")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "코스 생성 성공",
        content = @Content(schema = @Schema(implementation = CourseResponseDto.class))),
    @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음"),
    @ApiResponse(responseCode = "500", description = "서버 내부 오류 (스크래핑 실패 등)")
  })
  @PostMapping("/courses/empty")
  @SecurityRequirement(name = "JWT Auth")
  public CustomApiResponse<CourseResponseDto> createEmptyCourse(
      @RequestBody @Valid CourseRequestDto courseRequestDto,
      @Parameter(hidden = true) @AuthenticationPrincipal User user)
      throws IOException {

    return CustomApiResponse.of(
        SuccessStatus._CREATED,
        courseService.createEmptyCourse(user.getUsername(), courseRequestDto));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Operation(
      summary = "problem 추가",
      description = "코스에 problem을 추가 \n 기존에 존재하는 problem은 매핑, 존재하지 않으면 스크래핑")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "코스에 문제 추가 성공",
        content = @Content(schema = @Schema(implementation = CourseResponseDto.class))),
    @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음"),
    @ApiResponse(responseCode = "500", description = "서버 내부 오류 (스크래핑 실패 등)")
  })
  @PostMapping("/courses/{courseId}/problem")
  @SecurityRequirement(name = "JWT Auth")
  public CustomApiResponse<CourseResponseDto> addProblemToCourse(
      @RequestBody AddProblemToCourseRequestDto addProblemToCourseRequestDto,
      @Parameter(hidden = true) @AuthenticationPrincipal User user)
      throws IOException {

    return CustomApiResponse.onSuccess(
        adminService.addProblemToCourse(user.getUsername(), addProblemToCourseRequestDto));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("courses/{courseId}")
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
  public CustomApiResponse<CourseResponseDto> updateCourse(
      @PathVariable Long courseId, @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(courseService.updateCourse(user.getUsername(), courseId));
  }
}
