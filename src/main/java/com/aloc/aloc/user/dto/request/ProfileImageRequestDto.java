package com.aloc.aloc.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ProfileImageRequestDto {
  @Schema(description = "프로필이미지파일", example = "프로필이미지파일")
  private MultipartFile profileImageFile;
}
