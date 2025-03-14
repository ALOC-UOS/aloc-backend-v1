package com.aloc.aloc.algorithm.repository;

import com.aloc.aloc.algorithm.entity.Algorithm;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlgorithmRepository extends JpaRepository<Algorithm, Long> {
  Optional<Algorithm> findByAlgorithmId(Integer algorithmId);

  boolean existsByAlgorithmId(Integer algorithmId);
}
