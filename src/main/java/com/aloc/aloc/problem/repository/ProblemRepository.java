package com.aloc.aloc.problem.repository;

import com.aloc.aloc.problem.entity.Problem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

  // updatedAt이 현재 날짜 이후인 공개된 문제 찾기

  Optional<Problem> findByProblemId(Integer problemId);
}
