package com.aloc.aloc.usercourse.entity;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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
@Table(name = "user_course")
public class UserCourse extends AuditingTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id")
  private Course course;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Enumerated(EnumType.STRING)
  private UserCourseState userCourseState;

  private LocalDateTime closedAt;

  @Builder.Default
  @OneToMany(mappedBy = "userCourse", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserCourseProblem> userCourseProblemList = new ArrayList<>();

  public static UserCourse of(User user, Course course) {
    return UserCourse.builder()
        .course(course)
        .user(user)
        .userCourseState(UserCourseState.IN_PROGRESS)
        .closedAt(
            LocalDateTime.now()
                .plusDays(course.getDuration() - 1)
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999999999))
        .build();
  }

  public void addUserCourseProblem(UserCourseProblem userCourseProblem) {
    this.userCourseProblemList.add(userCourseProblem);
  }

  public void updateUserCourseState(UserCourseState userCourseState) {
    this.userCourseState = userCourseState;
  }
}
