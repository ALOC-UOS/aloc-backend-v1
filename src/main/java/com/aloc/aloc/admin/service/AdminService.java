package com.aloc.aloc.admin.service;

import com.aloc.aloc.admin.dto.request.AdminRoleChangeRequestDto;
import com.aloc.aloc.admin.dto.response.AdminCoinTransactionRequestDto;
import com.aloc.aloc.admin.dto.response.AdminCourseResponseDto;
import com.aloc.aloc.admin.dto.response.AdminDashboardResponseDto;
import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.entity.CoinHistory;
import com.aloc.aloc.coin.repository.CoinHistoryRepository;
import com.aloc.aloc.course.dto.response.RankResponseDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.CourseProblem;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.course.service.UserCourseService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.repository.UserRepository;
import com.aloc.aloc.user.service.UserService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
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
  private final CoinHistoryRepository coinHistoryRepository;
  private final UserRepository userRepository;

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

  @Transactional
  public String processCoinTransactions(String oauthId, AdminCoinTransactionRequestDto requestDto) {
    userService.validateAdmin(oauthId);

    List<CoinHistory> coinHistoryList = new ArrayList<>();
    List<User> updatedUserList = new ArrayList<>();

    for (UUID userId : requestDto.getUserIds()) {
      User user = userService.getUserById(userId);

      int current = user.getCoin();
      int addedCoin = requestDto.getCoin();
      int newBalance = current + addedCoin;
      if (newBalance < 0) {
        throw new IllegalArgumentException("차감되는 코인이 현재 코인코다 클 수 없습니다");
      }

      user.setCoin(newBalance);
      updatedUserList.add(user);

      coinHistoryList.add(
          CoinHistory.of(
              user,
              CoinResponseDto.of(
                  current, addedCoin, requestDto.getCoinType(), requestDto.getDescription())));
    }

    userRepository.saveAll(updatedUserList);
    coinHistoryRepository.saveAll(coinHistoryList);

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
}
