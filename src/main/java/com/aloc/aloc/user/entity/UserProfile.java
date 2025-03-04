package com.aloc.aloc.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  private Integer coin;

  @Column(nullable = false)
  private String profileColor;

  //  @Column(nullable = false)
  private String studentId;

  private String discordId;

  private String notionEmail;

  private String profileImageFileName;

  public void addCoin(int coinToAdd) {
    this.coin += coinToAdd;
  }
}
