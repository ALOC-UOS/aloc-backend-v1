package com.aloc.aloc.auth.controller;

import com.aloc.aloc.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "인증 관련 API 입니다.")
public class AuthController {
  private final UserService userService;

  @Operation(summary = "토큰 재발급", description = "Refresh Token을 이용해 Access Token을 재발급합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "AccessToken 재발급 성공"),
        @ApiResponse(responseCode = "401", description = "Refresh Token 만료 또는 유효하지 않음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
      })
  @SecurityRequirement(name = "Refresh Token")
  public void refresh() {}

  @Operation(summary = "로그아웃", description = "로그아웃 후 Refresh Token을 만료시킵니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
      })
  @PostMapping("/logout")
  @SecurityRequirement(name = "JWT Auth")
  public void logout(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
    userService.logout(user.getUsername());
  }
}
