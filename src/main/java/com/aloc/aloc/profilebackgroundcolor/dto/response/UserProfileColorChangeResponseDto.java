package com.aloc.aloc.profilebackgroundcolor.dto.response;

import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import com.aloc.aloc.user.dto.response.ColorResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserProfileColorChangeResponseDto {

  @Schema(description = "남은 코인", example = "90")
  private int userCoin;

  @Schema(description = "선택된 색상 정보")
  private ColorResponseDto color;

  public static UserProfileColorChangeResponseDto of(
      int userCoin, ProfileBackgroundColor profileBackgroundColor) {
    return UserProfileColorChangeResponseDto.builder()
        .userCoin(userCoin)
        .color(ColorResponseDto.of(profileBackgroundColor))
        .build();
  }
}
