package com.aloc.aloc.algorithm.service;

import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.repository.AlgorithmRepository;
import com.aloc.aloc.global.apipayload.exception.NotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlgorithmService {

  private final AlgorithmRepository algorithmRepository;

  public List<Algorithm> getAlgorithmsByIds(List<Integer> algorithmIdList) {
    // 1. 한 번의 쿼리로 모든 알고리즘 조회
    List<Algorithm> foundAlgorithms = algorithmRepository.findByAlgorithmIdIn(algorithmIdList);

    // 2. 조회된 알고리즘의 ID 추출
    Set<Integer> foundAlgorithmIds =
        foundAlgorithms.stream().map(Algorithm::getAlgorithmId).collect(Collectors.toSet());

    // 3. 존재하지 않는 ID 찾기
    List<Integer> notFoundIds =
        algorithmIdList.stream()
            .filter(id -> !foundAlgorithmIds.contains(id))
            .collect(Collectors.toList());

    // 4. 존재하지 않는 ID가 있으면 예외 발생
    if (!notFoundIds.isEmpty()) {
      throw new NotFoundException("존재하지 않은 알고리즘 아이디가 포함되어 있습니다: " + notFoundIds);
    }

    // 5. 원래 순서 유지하여 반환 (Optional)
    Map<Integer, Algorithm> algorithmMap =
        foundAlgorithms.stream()
            .collect(Collectors.toMap(Algorithm::getAlgorithmId, Function.identity()));

    return algorithmIdList.stream().map(algorithmMap::get).collect(Collectors.toList());
  }

  // [refactor] 알고리즘 조회 및 생성 메서드 분리
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
