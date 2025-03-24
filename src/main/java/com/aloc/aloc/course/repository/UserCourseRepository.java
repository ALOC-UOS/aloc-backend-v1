package com.aloc.aloc.course.repository;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
  List<UserCourse> findAllByUser(User user);

  List<UserCourse> findAllByUserAndUserCourseState(User user, UserCourseState userCourseState);

  List<UserCourse> findAllByUserAndClosedAtAfter(User user, LocalDateTime now);

  Optional<UserCourse> findByUserAndCourse(User user, Course course);

  @Query(
      "SELECT uc FROM UserCourse uc WHERE "
          + "uc.userCourseState = :userCourseState AND "
          + "uc.course.courseType = :courseType")
  List<UserCourse> findAllByUserCourseStateAndCourseType(
      @Param("userCourseState") UserCourseState userCourseState,
      @Param("courseType") CourseType courseType);

  List<UserCourse> findAllByUserCourseStateAndClosedAtBefore(
      UserCourseState userCourseState, LocalDateTime now);
}
