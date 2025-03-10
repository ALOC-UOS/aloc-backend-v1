package com.aloc.aloc.user.service;

import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.user.dto.request.UserRequestDto;
import com.aloc.aloc.user.dto.response.UserResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.repository.UserRepository;
import java.util.List;
import java.util.Set;
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

  @Transactional
  public void updateUserRank(User user, Integer rank) {
    user.setRank(rank);
    userRepository.save(user);
  }

  public List<User> getActiveUsers() {
    return userRepository.findAllByAuthorityIn(ACTIVE_AUTHORITIES);
  }

  public User findUser(String oauthId) {
    return userRepository
        .findByOauthId(oauthId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
  }

  @Transactional
  public void withdraw(String oauthId) {
    userRepository.deleteByOauthId(oauthId);
  }

  @Transactional
  public UserResponseDto updateUser(String oauthId, UserRequestDto userRequestDto) {
    User user = findUser(oauthId);
    user.setBaekjoonId(userRequestDto.getBaekjoonId());
    user.setName(userRequestDto.getName());
    user.setRank(baekjoonRankScrapingService.extractBaekjoonRank(user.getBaekjoonId()));
    userRepository.save(user);
    return UserResponseDto.of(user);
  }

  @Transactional
  public void logout(String oauthId) {
    User user = findUser(oauthId);
    user.destroyRefreshToken();
    userRepository.save(user);
  }
}
