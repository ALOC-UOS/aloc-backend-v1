package com.aloc.aloc.algorithm.dto.response;

import com.aloc.aloc.algorithm.entity.Algorithm;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AlgorithmResponseDto {
  private Long id;
  private int algorithmId;
  private String name;

  public static AlgorithmResponseDto of(Algorithm algorithm) {
    return AlgorithmResponseDto.builder()
        .id(algorithm.getId())
        .algorithmId(algorithm.getAlgorithmId())
        .name(algorithm.getKoreanName())
        .build();
  }
}
