package com.aloc.aloc.auth.controller;

import com.aloc.aloc.auth.dto.RefreshTokenDto;
import com.aloc.aloc.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2")
@Tag(name = "Auth API", description = "인증 관련 API 입니다.")
public class AuthController {
  private final AuthService authService;

  @SecurityRequirement(name = "JWT Auth")
  @DeleteMapping("/withdraw")
  @ApiResponse(responseCode = "200", description = "success")
  @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다.")
  public void withdraw(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
    authService.withdraw(user.getUsername());
  }

  @ApiResponse(responseCode = "200", description = "success")
  @Operation(summary = "토큰 재발급", description = "refreshToken으로 accessToken을 재발급합니다.")
  @PostMapping("/refresh")
  public void refresh(@RequestBody @Valid RefreshTokenDto refreshToken) {
    System.out.println("refresh" + refreshToken);
  }
}
