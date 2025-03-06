package com.aloc.aloc.user.entity;

import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.enums.Authority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  private String oauthId; // pk
  private String name; // name
  private String email; // email
  private String profileImageUrl; // image
  private Integer coin;

  @Column(nullable = false)
  private String profileColor;

  @Column(nullable = false)
  private String baekjoonId;

  private Integer rank;

  @Enumerated(EnumType.STRING)
  private Course course;

  @Enumerated(EnumType.STRING)
  private Authority authority;

  @Column(length = 1000)
  private String refreshToken;

  @Column private Integer solvedCount = 0;

  public void updateRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public void destroyRefreshToken() {
    this.refreshToken = null;
  }

  public void addSolvedCount() {
    this.solvedCount++;
  }

  @Builder
  public User(
      String baekjoonId,
      Integer rank,
      Course course,
      String oauthId,
      String name,
      String email,
      String profileImageUrl) {
    this.baekjoonId = baekjoonId;
    this.course = course;
    this.authority = Authority.ROLE_GUEST;
    this.rank = rank;
    this.oauthId = oauthId;
    this.name = name;
    this.email = email;
    this.profileImageUrl = profileImageUrl;
  }

  public User update(String userId, String nickname, String profileImageUrl) {
    this.name = userId;
    this.email = nickname;
    this.profileImageUrl = profileImageUrl;
    return this;
  }

  public static User create(UserOAuthProfile userOAuthProfile) {
    return User.builder()
        .name(userOAuthProfile.userId())
        .oauthId(userOAuthProfile.oauthId())
        .email(userOAuthProfile.nickname())
        .profileImageUrl(userOAuthProfile.profileImageUrl())
        .build();
  }

  public void addCoin(int coin) {
    this.coin += coin;
  }
}
