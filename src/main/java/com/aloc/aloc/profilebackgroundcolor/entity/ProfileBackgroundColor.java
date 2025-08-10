package com.aloc.aloc.profilebackgroundcolor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 프로필 배경 색상 엔티티
// color1, color2, color3, color4, color5 : 색상 코드
// type : common, rare, special
// degree : CSS 그라데이션 각도, 135, 180을 주로 사용
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileBackgroundColor {

  @Id private String name;

  @Column(nullable = false)
  private String color1;

  private String color2;
  private String color3;
  private String color4;
  private String color5;

  @Column(nullable = false)
  private String type;

  private Integer degree;
}
