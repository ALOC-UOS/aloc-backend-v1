package com.aloc.aloc.color;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
