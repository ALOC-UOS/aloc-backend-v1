package com.aloc.aloc.problem.repository;

import com.aloc.aloc.problem.entity.UserCourseProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCourseProblemRepository extends JpaRepository<UserCourseProblem, Long> {}
