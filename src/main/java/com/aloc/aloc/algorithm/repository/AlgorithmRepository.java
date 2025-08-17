package com.aloc.aloc.algorithm.repository;

import com.aloc.aloc.algorithm.entity.Algorithm;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlgorithmRepository extends JpaRepository<Algorithm, Long> {
  // 알고리즘 조회
  Optional<Algorithm> findByAlgorithmId(Integer algorithmId);

  // 알고리즘 존재 여부 확인
  boolean existsByAlgorithmId(Integer algorithmId);

  // 1번의 쿼리로 모든 알고리즘 조회
  List<Algorithm> findByAlgorithmIdIn(List<Integer> algorithmIds);
}
