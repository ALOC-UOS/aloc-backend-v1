package com.aloc.aloc.user.service;

import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
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

  public void checkAdmin(String oauthId) {
    Optional<User> userOptional = userRepository.findByOauthId(oauthId);
    if (userOptional.isEmpty()) {
      throw new IllegalStateException("로그인 정보가 없습니다.");
    }
    User user = userOptional.get();
    if (!Authority.ROLE_ADMIN.equals(user.getAuthority())) {
      throw new IllegalStateException("관리자만 가능합니다.");
    }
  }

  @Transactional
  public void checkUserRank(User user) {
    Integer rank = baekjoonRankScrapingService.extractBaekjoonRank(user.getBaekjoonId());
    if (!user.getRank().equals(rank)) {
      updateUserRank(user, rank);
    }
  }

  @Transactional
  public void updateUserRank(User user, Integer rank) {
    user.setRank(rank);
    userRepository.save(user);
  }

  public List<User> getActiveUsers() {
    return userRepository.findAllByAuthorityIn(ACTIVE_AUTHORITIES);
  }

  public List<User> getActiveUsersByCourse(Course course) {
    return userRepository.findAllByAuthorityInAndCourse(ACTIVE_AUTHORITIES, course);
  }

  public User findUser(String oauthId) {
    return userRepository
        .findByOauthId(oauthId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
  }

  public void saveUser(User user) {
    userRepository.save(user);
  }

  public void isActiveUser(User user) {
    if (!ACTIVE_AUTHORITIES.contains(user.getAuthority())) {
      throw new org.springframework.security.access.AccessDeniedException("해당 기능을 사용할 수 없는 유저입니다.");
    }
  }

  public User getActiveUser(String githubId) {
    User user = findUser(githubId);
    isActiveUser(user);
    return user;
  }
}
