package com.aloc.aloc.user.service;

import com.aloc.aloc.global.apipayload.exception.NotFoundException;
import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private static final Set<Authority> ACTIVE_AUTHORITIES =
      Set.of(Authority.ROLE_USER, Authority.ROLE_ADMIN);
  private final UserRepository userRepository;
  private final BaekjoonRankScrapingService baekjoonRankScrapingService;

  public List<User> getActiveUsers() {
    return userRepository.findAllByAuthorityIn(ACTIVE_AUTHORITIES);
  }

  public User getUser(String oauthId) {
    return userRepository
        .findByOauthId(oauthId)
        .orElseThrow(() -> new NotFoundException("해당 사용자가 존재하지 않습니다."));
  }

  public User getUserByUUID(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
  }

  @Transactional
  public void logout(String oauthId) {
    User user = getUser(oauthId);
    user.destroyRefreshToken();
    userRepository.save(user);
  }

  @Transactional
  public void saveUser(User user) {
    userRepository.save(user);
  }

  @Transactional
  public void updateUserCoin(User user, int coin) {
    user.addCoin(coin);
    saveUser(user);
  }

  public void checkBaekjoonId(String baekjoondId) {
    if (userRepository.existsByBaekjoonId(baekjoondId)) {
      throw new IllegalArgumentException("이미 존재하는 백준 아이디 입니다.");
    }
  }

  @Transactional
  public void initializeUserStreakDays() {
    getActiveUsers()
        .forEach(
            user -> {
              if (!isUserSolvedYesterday(user)) {
                user.setLastSolvedAt(null);
                user.setConsecutiveSolvedDays(0);
              }
            });
  }

  @Transactional
  public void updateUserBaekjoonRank(User user) {
    user.setRank(baekjoonRankScrapingService.extractBaekjoonRank(user.getBaekjoonId()));
  }

  private boolean isUserSolvedYesterday(User user) {
    LocalDateTime lastSolvedAt = user.getLastSolvedAt();
    return lastSolvedAt != null
        && lastSolvedAt.toLocalDate().equals(LocalDateTime.now().minusDays(1).toLocalDate());
  }

  public void validateAdmin(String oauthId) {
    if (!getUser(oauthId).getAuthority().equals(Authority.ROLE_ADMIN)) {
      throw new IllegalStateException("관리자만 이용가능한 서비스입니다.");
    }
  }

  public long getTotalUserCount() {
    return userRepository.countByAuthorityIn(ACTIVE_AUTHORITIES);
  }
}
