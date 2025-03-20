package com.aloc.aloc.user.dto.response;

import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import com.aloc.aloc.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserDetailResponseDto extends UserResponseDto {
  @Schema(description = "해결한 문제 수", example = "3")
  private final Integer solvedCount;

  @Schema(description = "연속문제 해결일 수", example = "2")
  private final Integer consecutiveSolvedDays;

  @Schema(description = "색상")
  private final ColorResponseDto color;

  @Schema(description = "유저 생성 일자", example = "2024-03-04T19:37:55")
  private final LocalDateTime createdAt;

  @Schema(description = "오늘 문제 해결 여부", example = "true")
  private boolean isTodaySolved;

  public static UserDetailResponseDto of(
      User user, ProfileBackgroundColor profileBackgroundColor, boolean isTodaySolved) {
    return UserDetailResponseDto.builder()
        .name(user.getName())
        .authority(user.getAuthority())
        .baekjoonId(user.getBaekjoonId())
        .rank(user.getRank())
        .coin(user.getCoin())
        .profileImageFileName(user.getProfileImageFileName())
        .solvedCount(user.getSolvedCount())
        .consecutiveSolvedDays(user.getConsecutiveSolvedDays())
        .color(ColorResponseDto.of(profileBackgroundColor))
        .isTodaySolved(isTodaySolved)
        .createdAt(user.getCreatedAt())
        .build();
  }
}
