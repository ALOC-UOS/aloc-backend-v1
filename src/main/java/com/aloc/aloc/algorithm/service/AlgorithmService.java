package com.aloc.aloc.algorithm.service;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.repository.AlgorithmRepository;
import com.aloc.aloc.scraper.AlgorithmScrapingService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlgorithmService {

  private final AlgorithmRepository algorithmRepository;
  private final AlgorithmScrapingService algorithmScrapingService;

  @Transactional
  public String createAlgorithm(String name) {
    Algorithm algorithm = algorithmScrapingService.scrapAlgorithmByName(name);
    if (existsByAlgorithmId(algorithm.getAlgorithmId())) {
      throw new IllegalArgumentException(
          "이미 존재하는 알고리즘입니다. 알고리즘 아이디 : " + algorithm.getAlgorithmId());
    }
    algorithmRepository.save(algorithm);
    return "알고리즘 스크래핑 완료";
  }

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

  public boolean existsByAlgorithmId(int algorithmId) {
    return algorithmRepository.existsByAlgorithmId(algorithmId);
  }
}
