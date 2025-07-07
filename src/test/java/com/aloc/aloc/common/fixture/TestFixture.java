package com.aloc.aloc.common.fixture;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.enums.CoinType;
import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.dto.response.RankResponseDto;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
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
    user.setBaekjoonId("baekjooid");
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

  public static ProblemSolvedResponseDto getMockProblemSolvedResponseDto() {
    return ProblemSolvedResponseDto.builder()
        .isSolved(true)
        .isCourseDone(true)
        .coinResponseDtos(getMockCoinResponseDto())
        .build();
  }
}
