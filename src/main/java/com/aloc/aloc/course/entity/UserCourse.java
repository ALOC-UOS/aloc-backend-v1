package com.aloc.aloc.course.entity;

import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.user.entity.User;
import jakarta.persistence.*;

@Entity
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
}
