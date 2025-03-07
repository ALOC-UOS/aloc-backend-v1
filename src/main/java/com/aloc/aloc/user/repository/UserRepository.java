package com.aloc.aloc.user.repository;

import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByRefreshToken(String refreshToken);

  Optional<User> findByOauthId(String oauthId);

  List<User> findAllByAuthorityIn(Set<Authority> authorities);

  void deleteByOauthId(String oauthId);
}
