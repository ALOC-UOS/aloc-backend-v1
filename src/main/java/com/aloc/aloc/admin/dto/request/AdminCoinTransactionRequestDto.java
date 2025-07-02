package com.aloc.aloc.admin.dto.request;

import com.aloc.aloc.coin.enums.CoinType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
@Schema(description = "유저들의 코인 지급 및 차감을 위한 request 입니다.")
public class AdminCoinTransactionRequestDto {
  @NotEmpty(message = "사용자 ID 목록은 필수입니다.")
  @Size(min = 1, message = "최소 1명의 사용자를 선택해야 합니다.")
  @Schema(name = "유저 아이디", example = "1234-5678-1234-5678")
  private List<UUID> userIds;

  @NotNull(message = "코인 타입은 필수 입니다.")
  @Schema(name = "코입 타입", example = "ADMIN_GRANT")
  private CoinType coinType;

  @NotNull(message = "코인은 필수 입니다.")
  @Schema(description = "지급/차감할 코인 양 (음수는 차감)", example = "100")
  private int coin;

  @NotNull(message = "지급 또는 차감하는 이유가 필요합니다.")
  @Schema(description = "지급/차감 이유 설명", example = "이벤트 보상 지급")
  private String description;
}
