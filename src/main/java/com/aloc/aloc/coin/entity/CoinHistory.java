package com.aloc.aloc.coin.entity;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.enums.CoinType;
import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CoinHistory extends AuditingTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  private Integer coin;

  @Enumerated(EnumType.STRING)
  private CoinType coinType;

  private String description;

  public static CoinHistory of(User user, CoinResponseDto coinResponseDto) {
    return CoinHistory.builder()
        .user(user)
        .coin(coinResponseDto.getAddedCoin())
        .coinType(coinResponseDto.getCoinType())
        .description(coinResponseDto.getDescription())
        .build();
  }
}
