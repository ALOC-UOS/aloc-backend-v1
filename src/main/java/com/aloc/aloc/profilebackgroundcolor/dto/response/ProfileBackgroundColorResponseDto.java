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
public class ProfileBackgroundColorResponseDto {
  @Schema(description = "유저의 남은 코인", example = "100")
  private int userCoin;

  @Schema(description = "색상")
  private ColorResponseDto color;

  public static ProfileBackgroundColorResponseDto of(
      int userCoin, ProfileBackgroundColor profileBackgroundColor) {
    return ProfileBackgroundColorResponseDto.builder()
        .userCoin(userCoin)
        .color(ColorResponseDto.of(profileBackgroundColor))
        .build();
  }
}
