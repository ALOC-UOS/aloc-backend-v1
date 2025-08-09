package com.aloc.aloc.user.dto.response;

import com.aloc.aloc.profilebackgroundcolor.entity.ProfileBackgroundColor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserColorChangeResponseDto {

  @Schema(description = "남은 코인", example = "90")
  private int userCoin;

  @Schema(description = "선택된 색상 정보")
  private ColorResponseDto color;

  public static UserColorChangeResponseDto of(
      int userCoin, ProfileBackgroundColor profileBackgroundColor) {
    return UserColorChangeResponseDto.builder()
        .userCoin(userCoin)
        .color(ColorResponseDto.of(profileBackgroundColor))
        .build();
  }
}
