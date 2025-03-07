package com.aloc.aloc.problem.entity;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.global.domain.AuditingTimeEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Problem extends AuditingTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private Integer rank;

  private Integer problemId;

  @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProblemAlgorithm> problemAlgorithmList = new ArrayList<>();

  @Builder
  public Problem(String title, Integer rank, Integer problemId, Course course) {
    this.title = title;
    this.rank = rank;
    this.problemId = problemId;
    this.problemAlgorithmList = new ArrayList<>();
  }

  public void addAllProblemAlgorithms(List<ProblemAlgorithm> problemAlgorithms) {
    this.problemAlgorithmList.addAll(problemAlgorithms);
  }
}
