package com.aloc.aloc.course.entity;

import com.aloc.aloc.admin.dto.request.EmptyCourseRequestDto;
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
  private String description;

  @Enumerated(EnumType.STRING)
  private CourseType courseType;

  private Integer problemCnt;
  private Integer minRank;
  private Integer maxRank;
  private Integer averageRank;
  private Long generateCnt;
  private Integer duration;
  private Long successCnt;

  @Builder.Default
  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CourseProblem> courseProblemList = new ArrayList<>();

  public static Course of(CourseRequestDto courseRequestDto) {
    return Course.builder()
        .title(courseRequestDto.getTitle())
        .description((courseRequestDto.getDescription()))
        .courseType(courseRequestDto.getType())
        .problemCnt(courseRequestDto.getProblemCnt())
        .minRank(courseRequestDto.getMinRank())
        .maxRank(courseRequestDto.getMaxRank())
        .generateCnt(0L)
        .successCnt(0L)
        .duration(
            courseRequestDto.getType().equals(CourseType.DAILY)
                ? courseRequestDto.getProblemCnt()
                : courseRequestDto.getDuration())
        .build();
  }

  public static Course ofEmpty(EmptyCourseRequestDto emptyCourseRequestDto) {
    return Course.builder()
        .title(emptyCourseRequestDto.getTitle())
        .description((emptyCourseRequestDto.getDescription()))
        .courseType(emptyCourseRequestDto.getType())
        .problemCnt(0)
        .minRank(0)
        .maxRank(0)
        .generateCnt(0L)
        .successCnt(0L)
        .duration(
            emptyCourseRequestDto.getType().equals(CourseType.DAILY)
                ? 0
                : emptyCourseRequestDto.getDuration())
        .build();
  }

  public void addAllCourseProblems(List<CourseProblem> courseProblemList) {
    this.courseProblemList.addAll(courseProblemList);
  }

  public void calculateAverageRank() {
    if (this.courseProblemList == null || this.courseProblemList.isEmpty()) {
      this.averageRank = 0;
      return;
    } // 빈 코스를 만들게 되면 averageRank에서 오류 발생 -> 이에 대한 예외처리

    Integer totalRank = 0;
    for (CourseProblem courseProblem : this.courseProblemList) {
      totalRank += courseProblem.getProblem().getRank();
    }
    this.averageRank = (int) totalRank / problemCnt;
  }

  public void updateRankRange() {
    // 최소 랭크 (가장 작은 rank 값)
    this.minRank =
        courseProblemList.stream()
            .mapToInt(courseProblem -> courseProblem.getProblem().getRank()) // problem의 rank를 추출
            .min() // 최소값
            .orElseThrow(() -> new IllegalStateException("No problems in the course")); // 없으면 예외 처리

    // 최대 랭크 (가장 큰 rank 값)
    this.maxRank =
        courseProblemList.stream()
            .mapToInt(courseProblem -> courseProblem.getProblem().getRank()) // problem의 rank를 추출
            .max() // 최대값
            .orElseThrow(() -> new IllegalStateException("No problems in the course")); // 없으면 예외 처리
  }

  public void addCourseProblem(CourseProblem courseProblem) {
    this.courseProblemList.add(courseProblem);
    this.problemCnt = this.courseProblemList.size();

    if (this.courseType.equals(CourseType.DAILY)) {
      this.duration = this.problemCnt;
    }
  }

  public void addGenerateCnt() {
    this.generateCnt += 1L;
  }

  public void addSuccessCnt() {
    this.successCnt += 1L;
  }
}
