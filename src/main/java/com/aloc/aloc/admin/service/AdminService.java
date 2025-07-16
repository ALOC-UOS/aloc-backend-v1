package com.aloc.aloc.admin.service;

import com.aloc.aloc.admin.dto.request.AdminCoinTransactionRequestDto;
import com.aloc.aloc.admin.dto.request.AdminRoleChangeRequestDto;
import com.aloc.aloc.admin.dto.request.EmptyCourseRequestDto;
import com.aloc.aloc.admin.dto.response.AdminCourseResponseDto;
import com.aloc.aloc.admin.dto.response.AdminDashboardResponseDto;
import com.aloc.aloc.admin.dto.response.AdminUserResponseDto;
import com.aloc.aloc.admin.dto.response.AdminWithdrawResponseDto;
import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.service.CoinService;
import com.aloc.aloc.course.dto.request.CourseRequestDto;
import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.dto.response.RankResponseDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.CourseProblem;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.course.service.UserCourseService;
import com.aloc.aloc.scraper.ProblemScrapingService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.service.UserService;
import com.aloc.aloc.user.service.facade.UserFacade;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {
  private final UserService userService;
  private final CourseService courseService;
  private final UserCourseService userCourseService;
  private final UserFacade userFacade;
  private final CoinService coinService;
  private final ProblemScrapingService problemScrapingService;

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

  public AdminWithdrawResponseDto killUser(String oauthId, UUID id) {
    userService.validateAdmin(oauthId); // admin인지 검사하고
    User user = userService.getUserByUUID(id); // id로 삭제할 유저를 가져오고
    userFacade.withdraw(user.getOauthId()); // userFacade로 삭제하기
    return AdminWithdrawResponseDto.of(user);
  }

  @Transactional
  public String processCoinTransactions(String oauthId, AdminCoinTransactionRequestDto requestDto) {
    userService.validateAdmin(oauthId);

    for (UUID userId : requestDto.getUserIds()) {
      User user = userService.getUserById(userId);
      CoinResponseDto coinResponseDto =
          CoinResponseDto.of(
              user.getCoin(),
              requestDto.getCoin(),
              requestDto.getCoinType(),
              requestDto.getDescription());

      coinService.updateUserCoin(user, coinResponseDto);
    }
    return "success";
  }

  @Transactional
  public String updateUserRole(
      String oauthId, AdminRoleChangeRequestDto adminRoleChangeRequestDto) {
    userService.validateAdmin(oauthId); // 요청자의 권한 검증
    // 바꾸려는 권한이 ROLE_NEW_USER 인지 검증
    if (adminRoleChangeRequestDto.getRole() == Authority.ROLE_NEW_USER) {
      throw new IllegalArgumentException("ROLE_NEW_USER 권한으로 변경은 허용되지 않습니다.");
    }

    List<User> users = new ArrayList<>();
    for (UUID uuid : adminRoleChangeRequestDto.getUserIds()) {
      User user = userService.getUserById(uuid);

      if (user.getAuthority() == Authority.ROLE_NEW_USER) {
        throw new IllegalArgumentException("백준에 연동되지 않은 회원은 권한 변경이 불가합니다.");
      }

      if (user.getAuthority() == adminRoleChangeRequestDto.getRole()) {
        continue;
      }

      user.setAuthority(adminRoleChangeRequestDto.getRole());
      users.add(user);
    }
    userService.saveAllUser(users);
    return "success";
  }

  @Transactional
  public List<AdminUserResponseDto> getAllUsersForAdmin(String oauthId) {
    userService.validateAdmin(oauthId);
    return userFacade.getAllUsers().stream()
        .map(AdminUserResponseDto::of)
        .collect(Collectors.toList());
  }

  @Transactional
  public CourseResponseDto addProblemToCourse(String username, Long courseId, int problemId)
      throws IOException {
    userService.validateAdmin(username);

    Course course = courseService.getCourseById(courseId);
    problemScrapingService.createCourseByProblemId(course, problemId);

    return CourseResponseDto.of(course, UserCourseState.NOT_STARTED);
  }

  public CourseResponseDto createCourse(String user, CourseRequestDto courseRequestDto)
      throws IOException {
    userService.validateAdmin(user); // user가 admin인지 검사
    Course course = courseService.createCourse(courseRequestDto);

    return CourseResponseDto.of(course, UserCourseState.NOT_STARTED);
  }

  @Transactional
  public CourseResponseDto createEmptyCourse(
      String user, EmptyCourseRequestDto emptyCourseRequestDto) {
    userService.validateAdmin(user);
    Course course = courseService.createEmptyCourse(emptyCourseRequestDto);
    return CourseResponseDto.of(course, UserCourseState.NOT_STARTED);
  }

  public CourseResponseDto updateCourse(String user, Long courseId) {
    userService.validateAdmin(user);

    Course course = courseService.updateCourse(courseId);
    return CourseResponseDto.of(course, UserCourseState.NOT_STARTED);
  }
}
