package com.aloc.aloc.profilebackgroundcolor.dto.response;

import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProfileColorListResponseDto {
  @Schema(description = "컬러 이름", example = "Red")
  private String name;

  @Schema(description = "첫번째 색상 코드", example = "#FF5A5A")
  private String color1;

  @Schema(description = "두번째 색상 코드", example = "#FF5A5A")
  private String color2;

  @Schema(description = "세번째 색상 코드", example = "#FF5A5A")
  private String color3;

  @Schema(description = "네번째 색상 코드", example = "#FF5A5A")
  private String color4;

  @Schema(description = "다섯번째 색상 코드", example = "#FF5A5A")
  private String color5;

  @Schema(description = "컬러 타입", example = "common")
  private String type;

  @Schema(description = "회전 각도", example = "135")
  private Integer degree;

  public static ProfileColorListResponseDto fromEntity(ProfileBackgroundColor color) {
    return ProfileColorListResponseDto.builder()
        .name(color.getName())
        .color1(color.getColor1())
        .color2(color.getColor2())
        .color3(color.getColor3())
        .color4(color.getColor4())
        .color5(color.getColor5())
        .type(color.getType())
        .degree(color.getDegree())
        .build();
  }
}
