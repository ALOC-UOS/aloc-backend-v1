package com.aloc.aloc.course.repository;

import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
  List<UserCourse> findAllByUser(User user);

  List<UserCourse> findAllByUserAndClosedAtAfter(User user, LocalDateTime now);
}
