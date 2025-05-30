package com.aloc.aloc.course.entity;

import com.aloc.aloc.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CourseProblem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id")
  private Course course;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "problem_id")
  private Problem problem;

  @Column(name = "problem_order")
  private Integer problemOrder;
}
