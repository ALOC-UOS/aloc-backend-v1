package com.aloc.aloc.course.service;

import com.aloc.aloc.course.dto.request.CourseRequestDto;
import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.course.repository.CourseRepository;
import com.aloc.aloc.scraper.ProblemScrapingService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import com.aloc.aloc.webhook.DiscordWebhookService;
import java.io.IOException;
import java.util.List;
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
  private final DiscordWebhookService discordWebhookService;
  private final UserCourseService userCourseService;
  private final UserService userService;

  public Page<CourseResponseDto> getCourses(Pageable pageable) {
    Page<Course> courses = courseRepository.findAll(pageable);
    return courses.map(course -> CourseResponseDto.of(course, false));
  }

  public Page<CourseResponseDto> getCoursesByUser(Pageable pageable, String oauthId) {
    User user = userService.findUser(oauthId);
    List<UserCourse> userCourses = userCourseService.getUserCoursesByUser(user);
    Page<Course> courses = courseRepository.findAll(pageable);

    return courses.map(
        course -> {
          boolean isSuccess =
              userCourses.stream()
                  .anyMatch(
                      userCourse ->
                          userCourse.getCourse().equals(course)
                              && userCourse.getUserCourseState() == UserCourseState.SUCCESS);

          return CourseResponseDto.of(course, isSuccess);
        });
  }

  @Transactional
  public CourseResponseDto createCourse(CourseRequestDto courseRequestDto) throws IOException {
    System.out.println("코스시작");
    Course course = Course.of(courseRequestDto);
    courseRepository.save(course);
    // 스크랩핑 로직 추가
    String message = problemScrapingService.createProblemsByCourse(course, courseRequestDto);
    discordWebhookService.sendNotification(message);
    return CourseResponseDto.of(course, false);
  }
}
