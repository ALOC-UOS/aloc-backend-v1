package com.aloc.aloc.course.entity;

import com.aloc.aloc.course.dto.request.CourseRequestDto;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.global.domain.AuditingTimeEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Course extends AuditingTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  @Enumerated(EnumType.STRING)
  private CourseType courseType;

  private Integer problemCnt;
  private Integer minRank;
  private Integer maxRank;
  private Integer averageRank;
  private Long generateCnt;
  private Integer duration;

  @Builder.Default
  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CourseProblem> courseProblemList = new ArrayList<>();

  public static Course of(CourseRequestDto courseRequestDto) {
    return Course.builder()
        .title(courseRequestDto.getTitle())
        .courseType(courseRequestDto.getCourseType())
        .problemCnt(courseRequestDto.getProblemCnt())
        .minRank(courseRequestDto.getMinRank())
        .maxRank(courseRequestDto.getMaxRank())
        .generateCnt(0L)
        .build();
  }

  public void addAllCourseProblems(List<CourseProblem> courseProblemList) {
    this.courseProblemList.addAll(courseProblemList);
  }

  public void calculateAverageRank() {
    Integer totalRank = 0;
    for (CourseProblem courseProblem : this.courseProblemList) {
      totalRank += courseProblem.getProblem().getRank();
    }
    this.averageRank = (int) totalRank / problemCnt;
  }
}
