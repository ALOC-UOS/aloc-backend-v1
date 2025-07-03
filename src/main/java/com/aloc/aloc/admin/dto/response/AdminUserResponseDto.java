package com.aloc.aloc.admin.dto.response;

import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminUserResponseDto {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

  @Schema(description = "유저 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
  private UUID id;

  @Schema(description = "유저 이름", example = "김철수")
  private String name;

  @Schema(description = "유저 이메일", example = "user1@example.com")
  private String email;

  @Schema(description = "유저 권한", example = "ROLE_USER")
  private Authority authority;

  @Schema(description = "랭크", example = "31")
  private Integer rank;

  @Schema(description = "코인", example = "100")
  private Integer coin;

  @Schema(
      description = "유저 생성 일자 (ISO-8601 UTC Z)",
      example = "2024-03-04T00:00:00.000Z")
  private String createdAt;

  @Schema(
      description = "마지막 문제 해결 일자 (ISO-8601 UTC Z) 또는 \"null\"",
      example = "2024-03-04T19:37:55.123Z")
  private String lastSolvedAt;

  public static AdminUserResponseDto of(User user) {

    String created =
        user.getCreatedAt() != null
            ? user.getCreatedAt().atOffset(ZoneOffset.UTC).format(FORMATTER)
            : "";

    String lastSolved =
        user.getLastSolvedAt() != null
            ? user.getLastSolvedAt().atOffset(ZoneOffset.UTC).format(FORMATTER)
            : "";

    return AdminUserResponseDto.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .authority(user.getAuthority())
        .rank(user.getRank())
        .coin(user.getCoin())
        .createdAt(created)
        .lastSolvedAt(lastSolved)
        .build();
  }
}
