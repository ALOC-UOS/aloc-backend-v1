package com.aloc.aloc.admin.service;

import com.aloc.aloc.admin.dto.response.AdminCourseListResponseDto;
import com.aloc.aloc.admin.dto.response.AdminDashboardResponseDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.CourseProblem;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.course.repository.CourseRepository;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.course.service.UserCourseService;
import com.aloc.aloc.user.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
  private final UserService userService;
  private final CourseService courseService;
  private final UserCourseService userCourseService;
  private final CourseRepository courseRepository;

  public AdminDashboardResponseDto getDashboard(String oauthId) {
    userService.validateAdmin(oauthId);
    return AdminDashboardResponseDto.of(
        userService.getTotalUserCount(),
        courseService.getActiveCourseCount(),
        userCourseService.getUserCourseCountByUserCourseState(UserCourseState.IN_PROGRESS),
        userCourseService.getUserCourseCountByUserCourseState(UserCourseState.SUCCESS),
        0L);
  }

  // algorithmIdList를 처리해 course의 다른 필드와 함께 하나의 Dto로 묶음
  private AdminCourseListResponseDto toAdminCourseListResponseDto(Course course) {
    List<Integer> algorithmIdList = course.getCourseProblemList().stream()
        .map(CourseProblem::getProblem)
        .flatMap(problem -> problem.getProblemAlgorithmList().stream())
        .map(pa -> pa.getAlgorithm().getAlgorithmId())
        .distinct()
        .collect(Collectors.toList());

    return AdminCourseListResponseDto.of(
        course.getTitle(),
        course.getCourseType(),
        course.getMinRank(),
        course.getMaxRank(),
        course.getAverageRank(),
        algorithmIdList,
        course.getGenerateCnt()
    );
  }

  public List<AdminCourseListResponseDto> getCourseList(String oauthId) {
    userService.validateAdmin(oauthId);
    List<Course> courseList = courseRepository.findAll();
    return courseList.stream()
        .map(this::toAdminCourseListResponseDto)
        .collect(Collectors.toList());
  }
}
