package com.aloc.aloc.user.service;

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
}
