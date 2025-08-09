package com.aloc.aloc.profilebackgroundcolor.dto.response;

import com.aloc.aloc.profilebackgroundcolor.entity.ProfileBackgroundColor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ColorResponseDto {
  @Schema(description = "프로필 색상이름", example = "Blue")
  private String name;

  @Schema(description = "색상 분류", example = "special")
  private final String type;

  @Schema(description = "색상 코드 1", example = "#FFB800")
  private final String color1;

  @Schema(description = "색상 코드 2", example = "#FF69F0")
  private final String color2;

  @Schema(description = "색상 코드 3", example = "#408CFF")
  private final String color3;

  @Schema(description = "색상 코드 4", example = "null")
  private final String color4;

  @Schema(description = "색상 코드 5", example = "null")
  private final String color5;

  @Schema(description = "그라데이션 기울기", example = "135")
  private final Integer degree;

  public static ColorResponseDto of(ProfileBackgroundColor color) {
    return ColorResponseDto.builder()
        .name(color.getName())
        .type(color.getType())
        .color1(color.getColor1())
        .color2(color.getColor2())
        .color3(color.getColor3())
        .color4(color.getColor4())
        .color5(color.getColor5())
        .degree(color.getDegree())
        .build();
  }
}