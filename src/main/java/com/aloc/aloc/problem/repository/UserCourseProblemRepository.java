package com.aloc.aloc.problem.repository;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCourseProblemRepository extends JpaRepository<UserCourseProblem, Long> {
  List<UserCourseProblem> findAllByProblemAndUserCourseProblemStatusOrderBySolvedAtDesc(
      Problem problem, UserCourseProblemStatus userCourseProblemStatus);

  @Query(
      "SELECT ucp FROM UserCourseProblem ucp "
          + "WHERE ucp.userCourse.user = :user "
          + "AND ucp.problem = :problem "
          + "AND ucp.userCourseProblemStatus = :status")
  Optional<UserCourseProblem> findByUserAndProblemAndStatus(
      @Param("user") User user,
      @Param("problem") Problem problem,
      @Param("status") UserCourseProblemStatus status);
}
