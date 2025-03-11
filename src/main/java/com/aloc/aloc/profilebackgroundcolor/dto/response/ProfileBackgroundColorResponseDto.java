package com.aloc.aloc.profilebackgroundcolor.dto.response;

import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProfileBackgroundColorResponseDto {
  @Schema(description = "유저의 남은 코인", example = "100")
  private int userCoin;

  @Schema(description = "프로필색상 이름", example = "blue")
  private String colorName;

  @Schema(description = "프로필색상 유형", example = "common")
  private String type;

  @Schema(description = "프로필색상코드1", example = "#FFFFFF")
  private String color1;

  @Schema(description = "프로필색상코드2", example = "#FFFFFF")
  private String color2;

  @Schema(description = "프로필색상코드3", example = "#FFFFFF")
  private String color3;

  @Schema(description = "프로필색상코드4", example = "#FFFFFF")
  private String color4;

  @Schema(description = "프로필색상코드5", example = "#FFFFFF")
  private String color5;

  @Schema(description = "기울기", example = "135")
  private int degree;

  public static ProfileBackgroundColorResponseDto of(
      int userCoin, ProfileBackgroundColor profileBackgroundColor) {
    return ProfileBackgroundColorResponseDto.builder()
        .userCoin(userCoin)
        .colorName(profileBackgroundColor.getName())
        .type(profileBackgroundColor.getType())
        .color1(profileBackgroundColor.getColor1())
        .color2(profileBackgroundColor.getColor2())
        .color3(profileBackgroundColor.getColor3())
        .color4(profileBackgroundColor.getColor4())
        .color5(profileBackgroundColor.getColor5())
        .degree(profileBackgroundColor.getDegree())
        .build();
  }
}
