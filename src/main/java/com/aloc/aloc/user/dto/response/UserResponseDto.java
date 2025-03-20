package com.aloc.aloc.user.dto.response;

import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
  @Schema(description = "유저 이름", example = "김철수")
  private String name;

  @Schema(description = "백준 ID", example = "baekjoonId")
  private String baekjoonId;

  @Schema(description = "프로필 색상이름", example = "blue")
  private String profileColor;

  @Schema(description = "유저 권한", example = "ROLE_USER")
  private Authority authority;

  @Schema(description = "랭크", example = "31")
  private Integer rank;

  @Schema(description = "코인", example = "100")
  private Integer coin;

  @Schema(description = "프로필 이미지 이름", example = "이미지이름")
  private String profileImageFileName;

  public static UserResponseDto of(User user) {
    return UserResponseDto.builder()
        .name(user.getName())
        .authority(user.getAuthority())
        .baekjoonId(user.getBaekjoonId())
        .profileColor(user.getProfileColor())
        .rank(user.getRank())
        .coin(user.getCoin())
        .profileImageFileName(user.getProfileImageFileName())
        .build();
  }
}
