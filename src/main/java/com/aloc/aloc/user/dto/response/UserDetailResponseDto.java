package com.aloc.aloc.user.dto.response;

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

  @Schema(description = "색상 분류", example = "special")
  private final String colorCategory;

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

  public static UserDetailResponseDto of(
      User user,
      String colorCategory,
      String color1,
      String color2,
      String color3,
      String color4,
      String color5,
      Integer degree) {
    return UserDetailResponseDto.builder()
        .username(user.getName())
        .authority(user.getAuthority())
        .baekjoonId(user.getBaekjoonId())
        .rank(user.getRank())
        .coin(user.getCoin())
        .profileImageUrl(user.getProfileImageUrl())
        .solvedCount(user.getSolvedCount())
        .colorCategory(colorCategory)
        .color1(color1)
        .color2(color2)
        .color3(color3)
        .color4(color4)
        .color5(color5)
        .degree(degree)
        .createdAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
        .build();
  }
}
