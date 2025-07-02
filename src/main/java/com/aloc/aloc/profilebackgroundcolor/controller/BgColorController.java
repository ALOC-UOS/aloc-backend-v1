package com.aloc.aloc.profilebackgroundcolor.controller;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.profilebackgroundcolor.service.ProfileBackgroundColorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class BgColorController {

  private final ProfileBackgroundColorService profileBackgroundColorService;

  @GetMapping("/colors")
  @Operation(summary = "전체 배경 색상 조회", description = "모든 프로필 배경 색상 정보를 리스트 형태로 반환합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "성공적으로 색상 목록을 반환합니다.",
            content =
                @Content(schema = @Schema(implementation = ProfileColorListResponseDto.class))),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않았거나 관리자 권한이 없는 경우",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      })
  public CustomApiResponse<List<ProfileColorListResponseDto>> getAllColors() {
    return CustomApiResponse.onSuccess(profileBackgroundColorService.getAllColors());
  }
}
