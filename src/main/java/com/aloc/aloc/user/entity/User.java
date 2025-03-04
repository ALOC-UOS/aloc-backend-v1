package com.aloc.aloc.user.entity;

import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.enums.Authority;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_user")
public class User extends AuditingTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //  @Column(nullable = false)
  private String username;

  private String oauthId;
  private String userId;
  private String nickname;
  private String profileImageUrl;

  //  @Column(nullable = false)
  private String baekjoonId;

  //  @Column(nullable = false)
  private String githubId;

  private Integer rank;

  @Enumerated(EnumType.STRING)
  private Course course;

  //  @Column(nullable = false)
  private String password = "password";

  @Enumerated(EnumType.STRING)
  private Authority authority;

  @Column(length = 1000)
  private String refreshToken;

  @Column private Integer solvedCount = 0;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private UserProfile userProfile;

  public void updateRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public void destroyRefreshToken() {
    this.refreshToken = null;
  }

  public void encodePassword(PasswordEncoder passwordEncoder) {
    this.password = passwordEncoder.encode(password);
  }

  public Boolean matchPassword(PasswordEncoder passwordEncoder, String password) {
    return passwordEncoder.matches(password, this.password);
  }

  public void addSolvedCount() {
    this.solvedCount++;
  }

  public void setProfileImageFileName(String fileName) {
    this.userProfile.setProfileImageFileName(fileName);
  }

  @Builder
  public User(
      String username,
      String baekjoonId,
      String githubId,
      String studentId,
      String password,
      String discordId,
      Integer rank,
      String notionEmail,
      Course course,
      String oauthId,
      String userId,
      String nickname,
      String profileImageUrl) {
    this.username = username;
    this.baekjoonId = baekjoonId;
    this.githubId = githubId;
    this.password = password;
    this.course = course;
    this.authority = Authority.ROLE_GUEST;
    this.rank = rank;
    this.oauthId = oauthId;
    this.userId = userId;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
    this.userProfile =
        UserProfile.builder()
            .user(this)
            .coin(0)
            .profileColor("Blue")
            .studentId(studentId)
            .discordId(discordId)
            .notionEmail(notionEmail)
            .build();
  }

  public User update(String userId, String nickname, String profileImageUrl) {
    this.userId = userId;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
    return this;
  }

  public static User create(UserOAuthProfile userOAuthProfile) {
    return User.builder()
        .userId(userOAuthProfile.userId())
        .oauthId(userOAuthProfile.oauthId())
        .nickname(userOAuthProfile.nickname())
        .profileImageUrl(userOAuthProfile.profileImageUrl())
        .build();
  }
}
