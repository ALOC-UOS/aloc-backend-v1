package com.aloc.aloc.auth.service;

import com.aloc.aloc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;

  @Transactional
  public void withdraw(String oauthId) {
    userRepository.deleteByOauthId(oauthId);
  }
}
