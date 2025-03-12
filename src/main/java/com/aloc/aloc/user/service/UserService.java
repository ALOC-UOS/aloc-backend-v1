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

  public List<User> getActiveUsers() {
    return userRepository.findAllByAuthorityIn(ACTIVE_AUTHORITIES);
  }

  public User getUser(String oauthId) {
    return userRepository
        .findByOauthId(oauthId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
  }

  @Transactional
  public void withdraw(String oauthId) {
    userRepository.deleteByOauthId(oauthId);
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
}
