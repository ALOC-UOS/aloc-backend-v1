package com.aloc.aloc.common.fixture;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.enums.CoinType;
import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.dto.response.RankResponseDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.usercourse.entity.UserCourse;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import java.time.LocalDateTime;
import java.util.List;

public class TestFixture {

  public static CourseResponseDto getMockCourseResponseDtoByStatus(UserCourseState state) {
    return CourseResponseDto.builder()
        .id(1L)
        .title("코스1")
        .description("기초 설명")
        .type(CourseType.DAILY)
        .problemCnt(3)
        .rank(RankResponseDto.of(1, 3, 2))
        .generateCnt(10L)
        .duration(3)
        .createdAt(LocalDateTime.of(2024, 3, 4, 19, 37, 55))
        .status(state)
        .successCnt(23L)
        .build();
  }

  public static User getMockNewUser() {
    return User.builder()
        .oauthId("oauth_123")
        .name("테스트 유저")
        .email("test@example.com")
        .profileImageFileName("test-profile.png")
        .build();
  }

  public static User getMockUserByOauthId(String oauthId) {
    User user =
        User.builder()
            .oauthId(oauthId)
            .name("테스트 유저")
            .email("test@example.com")
            .profileImageFileName("test-profile.png")
            .build();
    user.setAuthority(Authority.ROLE_USER);
    user.setBaekjoonId("baekjoonid");
    user.setRank(32);
    return user;
  }

  public static List<CoinResponseDto> getMockCoinResponseDto() {
    return List.of(
        CoinResponseDto.builder()
            .previousCoin(100)
            .addedCoin(10)
            .type(CoinType.SOLVE_REWARD)
            .description("문제 해결 보상")
            .build());
  }

  public static UserDetailResponseDto getMockUserDetailDto(User user) {
    return UserDetailResponseDto.builder()
        .name(user.getName())
        .authority(user.getAuthority())
        .baekjoonId(user.getBaekjoonId())
        .rank(user.getRank())
        .coin(user.getCoin())
        .profileImageFileName(user.getProfileImageFileName())
        .solvedCount(user.getSolvedCount())
        .consecutiveSolvedDays(user.getConsecutiveSolvedDays())
        .color(null)
        .isTodaySolved(false)
        .createdAt(user.getCreatedAt())
        .build();
  }

  public static ProblemSolvedResponseDto getMockProblemSolvedResponseDto() {
    return ProblemSolvedResponseDto.builder()
        .isSolved(true)
        .isCourseDone(true)
        .coinResponseDtos(getMockCoinResponseDto())
        .build();
  }

  public static Problem getMockProblem(int problemId, String title, int rank) {
    return Problem.builder().title(title).rank(rank).problemId(problemId).build();
  }

  public static UserCourse getMockUserCourse(
      User user, CourseType courseType, int problemCnt, int duration) {
    Course course =
        Course.builder()
            .title("테스트 코스")
            .description("테스트 설명")
            .courseType(courseType)
            .problemCnt(problemCnt)
            .duration(courseType == CourseType.DAILY ? problemCnt : duration)
            .minRank(1)
            .maxRank(1)
            .averageRank(1)
            .generateCnt(0L)
            .successCnt(0L)
            .build();
    return UserCourse.of(user, course);
  }

  public static UserCourseProblem getMockUserCourseProblem(
      UserCourse userCourse,
      Problem problem,
      UserCourseProblemStatus status,
      LocalDateTime solvedAt,
      int problemOrder) {
    return UserCourseProblem.builder()
        .userCourse(userCourse)
        .problem(problem)
        .userCourseProblemStatus(status)
        .solvedAt(solvedAt)
        .problemOrder(problemOrder)
        .build();
  }
}
