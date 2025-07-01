package com.aloc.aloc.admin.dto.request;

import com.aloc.aloc.user.enums.Authority;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminRoleChangeRequestDto {

  @Schema(description = "권한을 변경할 대상 사용자 ID 목록")
  @NotEmpty(message = "사용자 ID 목록은 비어있을 수 없습니다.")

  private List<UUID> userIds;

  @Schema(description = "새로 부여할 권한", example = "ROLE_ADMIN")
  @NotNull(message = "role은 필 수 값입니다.")
  private Authority role;
}
