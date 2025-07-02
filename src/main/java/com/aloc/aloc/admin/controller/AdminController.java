package com.aloc.aloc.admin.controller;

import com.aloc.aloc.admin.dto.response.AdminCourseResponseDto;
import com.aloc.aloc.admin.dto.response.AdminDashboardResponseDto;
import com.aloc.aloc.admin.dto.response.AdminWithdrawResponseDto;
import com.aloc.aloc.admin.service.AdminService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
  private final AdminService adminService;

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

  @DeleteMapping("admin/withdraw")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "유저 추방", description = "관리자가 유저를 추방합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "성공적으로 유저를 방출하였습니다.",
            content =
                @Content(schema = @Schema(implementation = AdminWithdrawResponseDto.class))),
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
}
