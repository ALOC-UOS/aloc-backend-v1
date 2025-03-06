package com.aloc.aloc.user.dto.request;

import com.aloc.aloc.coinhistory.enums.CoinType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserCoinDto {
  @Schema(description = "유저 oauthId", example = "oauthId")
  private String oauthId;

  @Schema(description = "추가/제거하려는 코인 값", example = "100")
  private int coin;

  @Schema(description = "코인 변경 종류", example = "ETC")
  private CoinType coinType;

  @Schema(description = "코인 업데이트 사유", example = "그냥 주고 싶어서")
  private String description;
}
