package com.aloc.aloc.course.repository;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

  Page<Course> findAllByCourseType(CourseType courseType, Pageable pageable);

  @Query(
      """
    SELECT DISTINCT c FROM Course c
    JOIN c.courseProblemList cp
    JOIN cp.problem p
    JOIN p.problemAlgorithmList pa
    WHERE pa.algorithm.id IN :algorithmIds
""")
  List<Course> findCoursesByAlgorithmIds(@Param("algorithmIds") List<Long> algorithmIds);
}
