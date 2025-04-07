package com.aloc.aloc.problem.repository;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCourseProblemRepository extends JpaRepository<UserCourseProblem, Long> {
  List<UserCourseProblem> findAllByProblemAndUserCourseProblemStatusOrderBySolvedAtDesc(
      Problem problem, UserCourseProblemStatus userCourseProblemStatus);

  Optional<UserCourseProblem> findByProblemAndUserCourseProblemStatus(
      Problem problem, UserCourseProblemStatus userCourseProblemStatus);
}
