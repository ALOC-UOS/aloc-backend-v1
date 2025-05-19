package com.aloc.aloc.algorithm.service;

import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.repository.AlgorithmRepository;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlgorithmService {

  private final AlgorithmRepository algorithmRepository;

  public List<Algorithm> getAlgorithmsByIds(List<Integer> algorithmIdList) {
    List<Algorithm> algorithms = new ArrayList<>();
    for (Integer algorithmId : algorithmIdList) {
      algorithms.add(
          algorithmRepository
              .findByAlgorithmId(algorithmId)
              .orElseThrow(() -> new NoSuchElementException("존재하지 않은 알고리즘 아이디가 포함되어 있습니다.")));
    }
    return algorithms;
  }

  public Algorithm getOrCreateAlgorithm(
      Integer algorithmId, String koreanName, String englishName) {
    return algorithmRepository
        .findByAlgorithmId(algorithmId)
        .orElseGet(
            () ->
                algorithmRepository.save(
                    Algorithm.builder()
                        .algorithmId(algorithmId)
                        .koreanName(koreanName)
                        .englishName(englishName)
                        .build()));
  }

  public List<AlgorithmResponseDto> getAlgorithms() {
    return algorithmRepository.findAll().stream().map(AlgorithmResponseDto::of).toList();
  }
}
