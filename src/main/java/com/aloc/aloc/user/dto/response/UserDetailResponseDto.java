package com.aloc.aloc.user.dto.response;

import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import com.aloc.aloc.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserDetailResponseDto extends UserResponseDto {
  @Schema(description = "해결한 문제 수", example = "3")
  private final Integer solvedCount;

  @Schema(description = "연속문제 해결일 수", example = "2")
  private final Integer consecutiveSolvedDays;

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

  @Schema(description = "유저 생성 일자", example = "2024-03-04T19:37:55")
  private final String createdAt;

  @Schema(description = "오늘 문제 해결 여부", example = "true")
  private boolean isTodaySolved;

  public static UserDetailResponseDto of(
      User user, ProfileBackgroundColor profileBackgroundColor, boolean isTodaySolved) {
    return UserDetailResponseDto.builder()
        .username(user.getName())
        .authority(user.getAuthority())
        .baekjoonId(user.getBaekjoonId())
        .rank(user.getRank())
        .coin(user.getCoin())
        .profileImageFileName(user.getProfileImageFileName())
        .solvedCount(user.getSolvedCount())
        .consecutiveSolvedDays(user.getConsecutiveSolvedDays())
        .type(profileBackgroundColor.getType())
        .color1(profileBackgroundColor.getColor1())
        .color2(profileBackgroundColor.getColor2())
        .color3(profileBackgroundColor.getColor3())
        .color4(profileBackgroundColor.getColor4())
        .color5(profileBackgroundColor.getColor5())
        .degree(profileBackgroundColor.getDegree())
        .isTodaySolved(isTodaySolved)
        .createdAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
        .build();
  }
}
