package com.aloc.aloc.course.repository;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
  List<UserCourse> findAllByUser(User user);

  List<UserCourse> findAllByUserAndClosedAtAfter(User user, LocalDateTime now);

  Optional<UserCourse> findByUserAndCourse(User user, Course course);

  List<UserCourse> findAllByUserCourseState(UserCourseState userCourseState);

  List<UserCourse> findAllByUserCourseStateAndClosedAtBefore(
      UserCourseState userCourseState, LocalDateTime now);
}
