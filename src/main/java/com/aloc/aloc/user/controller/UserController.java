package com.aloc.aloc.user.controller;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.service.UserFacade;
import com.aloc.aloc.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2")
@Tag(name = "User API", description = "User API 입니다.")
public class UserController {
  private final UserFacade userFacade;
  private final UserService userService;

  @GetMapping("/users")
  @Operation(summary = "유저 목록 조회", description = "전체 유저 목록을 조회합니다.")
  public CustomApiResponse<List<UserDetailResponseDto>> getUsers() {
    return CustomApiResponse.onSuccess(userFacade.getUsers());
  }

  @GetMapping("/user")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "유저 정보 조회", description = "유저의 개인 정보 목록을 조회합니다.")
  public CustomApiResponse<UserDetailResponseDto> getUser(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(userFacade.getUser(user.getUsername()));
  }

  @SecurityRequirement(name = "JWT Auth")
  @DeleteMapping("/withdraw")
  @ApiResponse(responseCode = "200", description = "success")
  @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다.")
  public void withdraw(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
    userService.withdraw(user.getUsername());
  }
}
