package com.aloc.aloc.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UserRequestDto {
  @Schema(description = "백준아이디", example = "baekjoonId")
  private String baekjoonId;

  @Schema(description = "사용자 닉네임", example = "이름")
  private String name;
}
