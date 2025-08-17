package com.aloc.aloc.algorithm.dto.response;

import com.aloc.aloc.algorithm.entity.Algorithm;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AlgorithmResponseDto {
  private Long id;
  private int algorithmId;
  private String koreanName;
  private String englishName;

  //Algorithm 엔티티 객체를 AlgorithmResponseDto 객체로 변환하는 메서드
  public static AlgorithmResponseDto of(Algorithm algorithm) {
    return AlgorithmResponseDto.builder()
        .id(algorithm.getId())
        .algorithmId(algorithm.getAlgorithmId())
        .koreanName(algorithm.getKoreanName())
        .englishName(algorithm.getEnglishName())
        .build();
  }
}
