package com.aloc.aloc.coin.dto.response;

import com.aloc.aloc.coin.enums.CoinType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CoinResponseDto {
  @Schema(description = "유저의 원래 코인", example = "100")
  private int previousCoin;

  @Schema(description = "획득한 코인", example = "10")
  private int addedCoin;

  @Schema(description = "코인 획득 유형", example = "SOLVE_REWARD")
  private CoinType type;

  @Schema(description = "설명", example = "문제 해결 보상")
  private String description;

  public static CoinResponseDto of(
      int previousCoin, int addedCoin, CoinType coinType, String description) {
    return CoinResponseDto.builder()
        .previousCoin(previousCoin)
        .addedCoin(addedCoin)
        .type(coinType)
        .description(description)
        .build();
  }
}
