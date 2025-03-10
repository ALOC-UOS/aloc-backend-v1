package com.aloc.aloc.color.controller;

import com.aloc.aloc.color.dto.response.ProfileBackgroundColorResponseDto;
import com.aloc.aloc.color.service.ProfileBackgroundColorService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/color")
@Tag(name = "ProfileBackgroundColor API", description = "ProfileBackgroundColor API 입니다.")
public class ProfileBackgroundColorController {
  private final ProfileBackgroundColorService profileBackgroundColorService;

  @PutMapping("/change")
  @SecurityRequirement(name = "JWT Auth")
  @SecurityRequirement(name = "Refresh Token")
  @Operation(summary = "프로필 색상 변경", description = "프로필 색상을 변경합니다.")
  public CustomApiResponse<ProfileBackgroundColorResponseDto> changeColor(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(
        profileBackgroundColorService.changeColor(user.getUsername()));
  }
}
