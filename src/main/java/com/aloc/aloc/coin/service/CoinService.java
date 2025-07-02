package com.aloc.aloc.coin.service;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.entity.CoinHistory;
import com.aloc.aloc.coin.repository.CoinHistoryRepository;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CoinService {
  private final UserService userService;
  private final CoinHistoryRepository coinHistoryRepository;
  private final List<CoinRewardPolicy> rewardPolicies;

  @Transactional
  public List<CoinResponseDto> rewardUser(User user, UserCourseProblem problem) {
    List<CoinResponseDto> responses = new ArrayList<>();
    for (CoinRewardPolicy policy : rewardPolicies) {
      if (policy.supports(user, problem)) {
        CoinResponseDto response = policy.apply(user, problem);
        userService.updateUserCoin(user, response.getAddedCoin());
        coinHistoryRepository.save(CoinHistory.of(user, response));
        responses.add(response);
      }
    }
    return responses;
  }

  @Transactional
  public void updateUserCoin(User user, CoinResponseDto coinResponseDto) {
    int current = user.getCoin();
    int addedCoin = coinResponseDto.getAddedCoin();
    if (current + addedCoin < 0) {
      throw new IllegalArgumentException("차감되는 코인이 현재 코인코다 클 수 없습니다");
    }

    userService.updateUserCoin(user, coinResponseDto.getAddedCoin());
    coinHistoryRepository.save(CoinHistory.of(user, coinResponseDto));
  }
}
