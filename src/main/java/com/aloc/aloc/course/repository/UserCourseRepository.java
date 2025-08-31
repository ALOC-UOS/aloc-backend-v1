package com.aloc.aloc.course.repository;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.usercourse.entity.UserCourse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
  List<UserCourse> findAllByUser(User user);

  List<UserCourse> findAllByUserAndUserCourseState(User user, UserCourseState userCourseState);

  Boolean existsByUserAndCourseAndUserCourseState(
      User user, Course course, UserCourseState userCourseState);

  Optional<UserCourse> findByUserAndCourseAndUserCourseState(
      User user, Course course, UserCourseState userCourseState);

  @Query(
      "SELECT uc FROM UserCourse uc WHERE "
          + "uc.userCourseState = :userCourseState AND "
          + "uc.course.courseType = :courseType")
  List<UserCourse> findAllByUserCourseStateAndCourseType(
      @Param("userCourseState") UserCourseState userCourseState,
      @Param("courseType") CourseType courseType);

  List<UserCourse> findAllByUserCourseStateAndClosedAtBefore(
      UserCourseState userCourseState, LocalDateTime now);

  @Query(
      """
    SELECT COUNT(uc) + 1
    FROM UserCourse uc
    WHERE uc.course = :course
      AND uc.userCourseState = com.aloc.aloc.course.enums.UserCourseState.SUCCESS
      AND uc.updatedAt < :updatedAt
""")
  int findClearRank(@Param("course") Course course, @Param("updatedAt") LocalDateTime updatedAt);

  long countByUserCourseState(UserCourseState userCourseState);

  @Query("""
    SELECT uc FROM UserCourse uc
    WHERE uc.user = :user AND uc.course.id IN :courseIds
    AND uc.createdAt = (
      SELECT MAX(uc2.createdAt)
      FROM UserCourse uc2
      WHERE uc2.user = :user AND uc2.course.id = uc.course.id
    )
    """)
  List<UserCourse> findLatestUserCoursesByUserAndCourseIds(
      @Param("user") User user, 
      @Param("courseIds") List<Long> courseIds);
}
