package com.aloc.aloc.admin.dto.response;

import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminWithdrawDto {
  private UUID userId;
  private String name;
  private Authority authority;

  public static AdminWithdrawDto of(User user) {
    return AdminWithdrawDto.builder()
        .userId(user.getId())
        .name(user.getName())
        .authority(user.getAuthority())
        .build();
  }
}
