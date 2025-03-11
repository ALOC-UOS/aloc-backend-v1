package com.aloc.aloc.problem.entity;

import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCourseProblem extends AuditingTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "problem_id")
  private Problem problem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_course_id")
  private UserCourse userCourse;

  @Enumerated(EnumType.STRING)
  private UserCourseProblemStatus userCourseProblemStatus;

  private LocalDateTime solvedAt;

  public static UserCourseProblem of(UserCourse userCourse, Problem problem) {
    return UserCourseProblem.builder()
        .problem(problem)
        .userCourse(userCourse)
        .userCourseProblemStatus(UserCourseProblemStatus.HIDDEN)
        .build();
  }

  public void updateUserCourseProblemStatus(UserCourseProblemStatus userCourseProblemStatus) {
    this.userCourseProblemStatus = userCourseProblemStatus;
  }
}
