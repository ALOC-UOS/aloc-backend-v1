package com.aloc.aloc.global.login.service;

import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.repository.UserRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String oauthId) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByOauthId(oauthId)
            .orElseThrow(() -> new UsernameNotFoundException("사용자 정보가 없습니다."));
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getAuthority().toString());
    return new org.springframework.security.core.userdetails.User(
        user.getOauthId(), "", Collections.singleton(grantedAuthority));
  }
}
