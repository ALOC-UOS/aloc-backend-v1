package com.aloc.aloc.problem.repository;

import com.aloc.aloc.problem.entity.UserProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProblemRepository extends JpaRepository<UserProblem, Long> {}
