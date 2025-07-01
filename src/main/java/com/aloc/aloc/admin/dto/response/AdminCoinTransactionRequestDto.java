package com.aloc.aloc.admin.dto.response;

import com.aloc.aloc.coin.enums.CoinType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
@Schema(description = "유저들의 코인 지급 및 차감을 위한 request 입니다.")
public class AdminCoinTransactionRequestDto {
  @Schema(name = "유저 아이디", example = "1234-5678-1234-5678")
  private List<UUID> userIds;

  private CoinType coinType;
  private int coin;
  private String description;
}
