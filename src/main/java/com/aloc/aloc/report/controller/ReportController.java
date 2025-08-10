package com.aloc.aloc.report.controller;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.global.apipayload.status.SuccessStatus;
import com.aloc.aloc.report.dto.request.ReportRequestDto;
import com.aloc.aloc.report.dto.response.ReportResponseDto;
import com.aloc.aloc.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Report API", description = "문의사항 API 입니다.")
@RequestMapping("/api")
public class ReportController {

  private final ReportService reportService;

  @PostMapping("/report")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(
      summary = "문의사항 생성",
      description = "새로운 문의사항을 생성합니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "문의사항 생성 성공",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
      })
  public CustomApiResponse<String> createReport(
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @Valid @RequestBody ReportRequestDto reportRequestDto) {
    return CustomApiResponse.of(
        SuccessStatus._CREATED, reportService.createReport(user.getUsername(), reportRequestDto));
  }

  @GetMapping("/reports")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(
      summary = "유저가 자신의 문의사항 조회",
      description = "로그인한 사용자가 자신이 작성한 문의사항 목록을 조회합니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "문의사항 목록 조회 성공",
            content =
                @Content(
                    array =
                        @ArraySchema(schema = @Schema(implementation = ReportResponseDto.class)))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
      })
  public CustomApiResponse<List<ReportResponseDto>> getUserReports(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(reportService.getUserReports(user.getUsername()));
  }

  @DeleteMapping("/reports/{reportId}")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(
      summary = "문의사항 삭제(소프트 딜리트)",
      description = "사용자가 자신이 작성한 문의사항을 삭제합니다. 실제 삭제가 아닌 상태를 DELETED로 변경합니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "문의사항 삭제 성공",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "이미 삭제된 문의사항"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "문의사항 또는 사용자 정보 없음")
      })
  public CustomApiResponse<String> deleteReport(
      @PathVariable Long reportId, @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(reportService.deleteReport(reportId, user.getUsername()));
  }
}
