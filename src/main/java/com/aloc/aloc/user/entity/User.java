package com.aloc.aloc.user.entity;

import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.user.enums.Authority;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_user")
public class User extends AuditingTimeEntity {

  @Id
  @UuidGenerator
  @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
  private UUID id;

  private String oauthId; // pk
  private String name; // name
  private String email; // email
  private String profileImageFileName; // image
  private Integer coin;

  private String profileColor;

  private String baekjoonId;

  private Integer rank;

  @Enumerated(EnumType.STRING)
  private Authority authority;

  @Column(length = 1000)
  private String refreshToken;

  @Column private Integer solvedCount = 0;
  private Integer consecutiveSolvedDays = 0;
  private LocalDateTime lastSolvedAt;
  private LocalDateTime deletedAt;

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
      String oauthId,
      String name,
      String email,
      String profileImageFileName) {
    this.baekjoonId = baekjoonId;
    this.authority = Authority.ROLE_USER;
    this.rank = rank;
    this.oauthId = oauthId;
    this.name = name;
    this.email = email;
    this.profileImageFileName = profileImageFileName;
    this.coin = 0;
    this.profileColor = "Blue";
  }

  public User update(String email) {
    this.email = email;
    return this;
  }

  public static User create(UserOAuthProfile userOAuthProfile) {
    return User.builder()
        .name(userOAuthProfile.userId())
        .oauthId(userOAuthProfile.oauthId())
        .email(userOAuthProfile.nickname())
        .build();
  }

  public void addCoin(int coin) {
    this.coin += coin;
  }
}
