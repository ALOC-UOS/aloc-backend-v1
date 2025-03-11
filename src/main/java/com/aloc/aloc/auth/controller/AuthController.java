package com.aloc.aloc.auth.controller;

import com.aloc.aloc.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "인증 관련 API 입니다.")
public class AuthController {
  private final UserService userService;

  @ApiResponse(responseCode = "200", description = "success")
  @Operation(summary = "토큰 재발급", description = "refreshToken으로 accessToken을 재발급합니다.")
  @PostMapping("/api/refresh")
  @SecurityRequirement(name = "Refresh Token")
  public void refresh() {}

  @ApiResponse(responseCode = "200", description = "success")
  @Operation(summary = "로그아웃", description = "로그아웃합니다..")
  @PostMapping("/api/logout")
  @SecurityRequirement(name = "JWT Auth")
  public void logout(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
    userService.logout(user.getUsername());
  }
}
