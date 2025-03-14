package com.aloc.aloc.algorithm.entity;

import com.aloc.aloc.global.domain.AuditingTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Algorithm extends AuditingTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private Integer algorithmId;

  @Column(nullable = false)
  private String koreanName;

  @Column(nullable = false)
  private String englishName;
}
