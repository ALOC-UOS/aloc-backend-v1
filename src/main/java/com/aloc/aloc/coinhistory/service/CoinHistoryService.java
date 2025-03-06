package com.aloc.aloc.coinhistory.service;

import com.aloc.aloc.coinhistory.CoinHistory;
import com.aloc.aloc.coinhistory.dto.response.CoinHistoryContentDto;
import com.aloc.aloc.coinhistory.dto.response.CoinHistoryResponseDto;
import com.aloc.aloc.coinhistory.enums.CoinType;
import com.aloc.aloc.coinhistory.repository.CoinHistoryRepository;
import com.aloc.aloc.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoinHistoryService {
  private final CoinHistoryRepository coinHistoryRepository;

  @Transactional
  public void addCoinHistory(User user, int coin, CoinType coinType, String description) {
    CoinHistory coinHistory =
        CoinHistory.builder()
            .coin(coin)
            .coinType(coinType)
            .user(user)
            .description(description)
            .build();
    coinHistoryRepository.save(coinHistory);
  }

  public CoinHistoryResponseDto getUserCoinHistory(User user) {
    List<CoinHistory> coinHistories =
        coinHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
    return CoinHistoryResponseDto.builder()
        .userCoin(user.getCoin())
        .histories(CoinHistoryContentDto.listOf(coinHistories))
        .build();
  }
}
