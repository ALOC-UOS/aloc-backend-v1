package com.aloc.aloc.admin.service;

import com.aloc.aloc.admin.dto.request.AdminRoleChangeRequestDto;
import com.aloc.aloc.admin.dto.response.AdminCourseListResponseDto;
import com.aloc.aloc.admin.dto.response.AdminDashboardResponseDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.CourseProblem;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.course.repository.CourseRepository;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.course.service.UserCourseService;
import com.aloc.aloc.global.apipayload.exception.BadRequestException;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
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
    List<Integer> algorithmIdList =
        course.getCourseProblemList().stream()
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
        course.getGenerateCnt());
  }

  public List<AdminCourseListResponseDto> getCourseList(String oauthId) {
    userService.validateAdmin(oauthId);
    List<Course> courseList = courseRepository.findAll();
    return courseList.stream().map(this::toAdminCourseListResponseDto).collect(Collectors.toList());
  }

  @Transactional
  public String updateUserRole(String oauthId, AdminRoleChangeRequestDto adminRoleChangeRequestDto) {
	  userService.validateAdmin(oauthId); // api 요청자가 admin 인지 체크
	  List<User> users = new ArrayList<>();
	  for (UUID uuid : adminRoleChangeRequestDto.getUserIds()) {
		  // 해당 uuid로 이 유저가 존재하는지 존재하면 가져오고 존크하지 않으면  error -> bad request 400
		  User user = userService.getUserById(uuid);
		  // 만약에 해당 유저의 권한이 ROLE_NEW_USER면 -> 백준 아이디가 널인 상태! 그러면 얘는 못바꿔  -> bad request 400
		  if(user.getAuthority()== Authority.ROLE_NEW_USER){
			  throw new BadRequestException("백준에 연동되지 않은 회원은 권한 변경이 불가합니다.");
		  }
		  if(user.getAuthority()==adminRoleChangeRequestDto.getRole()){
			continue;
		  }
		  user.setAuthority(adminRoleChangeRequestDto.getRole());
		  users.add(user);
	  }
	  userService.saveAllUser(users);
	  return "success";
  }
}
