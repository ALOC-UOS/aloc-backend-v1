package com.aloc.aloc.course.service;

import com.aloc.aloc.course.dto.request.CourseRequestDto;
import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.course.repository.CourseRepository;
import com.aloc.aloc.scraper.ProblemScrapingService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import com.aloc.aloc.usercourse.entity.UserCourse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {
  private final CourseRepository courseRepository;
  private final ProblemScrapingService problemScrapingService;

  private final UserCourseService userCourseService;
  private final UserService userService;

  public Page<CourseResponseDto> getCourses(Pageable pageable, CourseType courseTypeOrNull) {
    Page<Course> courses = getCoursePageByCourseType(pageable, courseTypeOrNull);
    return courses.map(course -> CourseResponseDto.of(course, UserCourseState.NOT_STARTED));
  }

  @Transactional
  public CourseResponseDto updateCourse(Long courseId) {
    Course course = getCourseById(courseId);
    course.updateRankRange();
    courseRepository.save(course);
    return CourseResponseDto.of(course, UserCourseState.NOT_STARTED);
  }

  private Page<Course> getCoursePageByCourseType(Pageable pageable, CourseType courseTypeOrNull) {
    return courseTypeOrNull == null
        ? courseRepository.findAll(pageable)
        : courseRepository.findAllByCourseType(courseTypeOrNull, pageable);
  }

  public Page<CourseResponseDto> getCoursesByUser(
      Pageable pageable, String oauthId, CourseType courseTypeOrNull) {
    User user = userService.getUser(oauthId);
    List<UserCourse> userCourses = userCourseService.getUserCoursesByUser(user);
    Page<Course> courses = getCoursePageByCourseType(pageable, courseTypeOrNull);

    return courses.map(
        course -> {
          Optional<UserCourse> latestUserCourse =
              userCourses.stream()
                  .filter(userCourse -> userCourse.getCourse().equals(course))
                  .max(Comparator.comparing(UserCourse::getCreatedAt)); // 최신 createdAt 찾기

          UserCourseState latestState =
              latestUserCourse
                  .map(UserCourse::getUserCourseState)
                  .orElse(UserCourseState.NOT_STARTED); // 만약 없다면 null 처리

          return CourseResponseDto.of(course, latestState);
        });
  }

  @Transactional
  public CourseResponseDto createCourse(CourseRequestDto courseRequestDto) throws IOException {
    System.out.println("코스시작");
    Course course = Course.of(courseRequestDto);
    courseRepository.save(course);
    // 스크랩핑 로직 추가
    problemScrapingService.createProblemsByCourse(course, courseRequestDto);
    return CourseResponseDto.of(course, UserCourseState.NOT_STARTED);
  }

  public Course getCourseById(Long courseId) {
    return courseRepository
        .findById(courseId)
        .orElseThrow(() -> new NoSuchElementException("해당 코스 아이디로 된 코스가 존재하지 않습니다."));
  }

  public List<Course> getRecommendedCourses(Course course) {

    List<Long> algorithmIds =
        course.getCourseProblemList().stream()
            .flatMap(cp -> cp.getProblem().getProblemAlgorithmList().stream())
            .map(pa -> pa.getAlgorithm().getId())
            .distinct()
            .toList();

    return courseRepository.findCoursesByAlgorithmIds(algorithmIds);
  }
}
