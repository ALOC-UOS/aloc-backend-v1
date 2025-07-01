package com.aloc.aloc.admin.service;

import com.aloc.aloc.admin.dto.response.AdminCourseResponseDto;
import com.aloc.aloc.admin.dto.response.AdminDashboardResponseDto;
import com.aloc.aloc.course.dto.response.RankResponseDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.CourseProblem;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.course.service.UserCourseService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import com.aloc.aloc.user.service.facade.UserFacade;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
  private final UserService userService;
  private final CourseService courseService;
  private final UserCourseService userCourseService;
  private final UserFacade userFacade;

  public AdminDashboardResponseDto getDashboard(String oauthId) {
    userService.validateAdmin(oauthId);
    return AdminDashboardResponseDto.of(
        userService.getTotalUserCount(),
        courseService.getActiveCourseCount(),
        userCourseService.getUserCourseCountByUserCourseState(UserCourseState.IN_PROGRESS),
        userCourseService.getUserCourseCountByUserCourseState(UserCourseState.SUCCESS),
        0L);
  }

  private AdminCourseResponseDto toAdminCourseListResponseDto(Course course) {
    List<String> algorithmList =
        course.getCourseProblemList().stream()
            .map(CourseProblem::getProblem)
            .flatMap(problem -> problem.getProblemAlgorithmList().stream())
            .map(pa -> pa.getAlgorithm().getKoreanName())
            .distinct()
            .collect(Collectors.toList());

    RankResponseDto rankResponseDto =
        RankResponseDto.of(course.getMinRank(), course.getMaxRank(), course.getAverageRank());

    return AdminCourseResponseDto.of(
        course.getTitle(),
        course.getCourseType(),
        rankResponseDto,
        algorithmList,
        course.getGenerateCnt());
  }

  public List<AdminCourseResponseDto> getCourseList(String oauthId) {
    userService.validateAdmin(oauthId);
    List<Course> courseList = courseService.getActiveCourses();
    return courseList.stream().map(this::toAdminCourseListResponseDto).collect(Collectors.toList());
  }

  public String killUser(String oauthId, UUID id) {
    userService.validateAdmin(oauthId); // admin인지 검사하고
    User deleteuser = userService.getUserByUUID(id); // id로 삭제할 유저를 가져오고
    userFacade.withdraw(deleteuser.getOauthId()); // userFacade로 삭제하기
    return "success";
  }
}
