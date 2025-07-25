package com.aloc.aloc.problem.service.facade;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.enums.CoinType;
import com.aloc.aloc.coin.service.CoinService;
import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.global.apipayload.exception.AlreadySolvedProblemException;
import com.aloc.aloc.global.apipayload.exception.ProblemNotYetSolvedException;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problem.service.UserCourseProblemService;
import com.aloc.aloc.scraper.SolvedCheckingService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import com.aloc.aloc.usercourse.entity.UserCourse;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProblemFacadeTest {

  @Mock private ProblemService problemService;
  @Mock private UserService userService;
  @Mock private UserCourseProblemService userCourseProblemService;
  @Mock private SolvedCheckingService solvedCheckingService;
  @Mock private CoinService coinService;

  @InjectMocks private ProblemFacade problemFacade;

  private final String oauthId = "oauth_123";
  private final Integer problemId = 123;

  @Test
  @DisplayName("checkProblemSolved: 이미 해결된 문제이면 AlreadySolvedProblemException 발생")
  void checkProblemSolvedAlreadySolved() {
    // given
    User user = TestFixture.getMockUserByOauthId(oauthId);
    Problem problem = TestFixture.getMockProblem(problemId, "문제", 1);
    UserCourse userCourse = TestFixture.getMockUserCourse(user, CourseType.DAILY, 3, 3);
    UserCourseProblem ucp =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem, UserCourseProblemStatus.SOLVED, LocalDateTime.now(), 1);

    given(userService.getUser(oauthId)).willReturn(user);
    given(problemService.getProblemByProblemId(problemId)).willReturn(problem);
    given(userCourseProblemService.getUserCourseProblemByProblem(problem, user)).willReturn(ucp);

    // when & then
    assertThatThrownBy(() -> problemFacade.checkProblemSolved(problemId, oauthId))
        .isInstanceOf(AlreadySolvedProblemException.class)
        .hasMessage("이미 해결한 문제입니다.");

    verify(userService, never()).updateUserBaekjoonRank(any());
    verify(solvedCheckingService, never()).isProblemSolved(any(), any(), any());
    verify(coinService, never()).rewardUser(any(), any());
  }

  @Test
  @DisplayName("checkProblemSolved: 아직 풀이되지 않은 문제이면 ProblemNotYetSolvedException 발생")
  void checkProblemSolvedNotYetSolved() {
    // given
    User user = TestFixture.getMockUserByOauthId(oauthId);
    Problem problem = TestFixture.getMockProblem(problemId, "문제", 1);
    UserCourse userCourse = TestFixture.getMockUserCourse(user, CourseType.DAILY, 3, 3);
    UserCourseProblem ucp =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem, UserCourseProblemStatus.UNSOLVED, null, 1);

    given(userService.getUser(oauthId)).willReturn(user);
    given(problemService.getProblemByProblemId(problemId)).willReturn(problem);
    given(userCourseProblemService.getUserCourseProblemByProblem(problem, user)).willReturn(ucp);
    given(solvedCheckingService.isProblemSolved(user.getBaekjoonId(), problem, ucp))
        .willReturn(false);

    // when & then
    assertThatThrownBy(() -> problemFacade.checkProblemSolved(problemId, oauthId))
        .isInstanceOf(ProblemNotYetSolvedException.class)
        .hasMessage("아직 문제의 채점이 완료되지 않았거나 해결되지 않았습니다.");

    verify(userService, never()).updateUserBaekjoonRank(any());
    verify(solvedCheckingService).isProblemSolved(user.getBaekjoonId(), problem, ucp);
    verify(coinService, never()).rewardUser(any(), any());
  }

  @Test
  @DisplayName("checkProblemSolved: DEADLINE 코스 마지막 문제 정상 처리 및 COURSE_REWARD 포함")
  void checkProblemSolvedDeadlineLast() {
    // given
    User user = TestFixture.getMockUserByOauthId(oauthId);
    Problem problem = TestFixture.getMockProblem(problemId, "문제", 1);
    UserCourse userCourse = TestFixture.getMockUserCourse(user, CourseType.DEADLINE, 3, 3);
    UserCourseProblem ucp =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem, UserCourseProblemStatus.UNSOLVED, null, 3);
    List<CoinResponseDto> rewards =
        List.of(
            CoinResponseDto.of(0, 10, CoinType.SOLVE_REWARD, "solve"),
            CoinResponseDto.of(10, 50, CoinType.COURSE_REWARD, "course complete"));

    given(userService.getUser(oauthId)).willReturn(user);
    given(problemService.getProblemByProblemId(problemId)).willReturn(problem);
    given(userCourseProblemService.getUserCourseProblemByProblem(problem, user)).willReturn(ucp);
    given(solvedCheckingService.isProblemSolved(user.getBaekjoonId(), problem, ucp))
        .willReturn(true);
    given(coinService.rewardUser(user, ucp)).willReturn(rewards);
    given(userCourseProblemService.getByUserCourse(userCourse)).willReturn(List.of(ucp));

    // when
    ProblemSolvedResponseDto response = problemFacade.checkProblemSolved(problemId, oauthId);

    // then
    assertThat(ucp.getUserCourseProblemStatus()).isEqualTo(UserCourseProblemStatus.SOLVED);
    assertThat(user.getSolvedCount()).isEqualTo(1);
    assertThat(userCourse.getUserCourseState()).isEqualTo(UserCourseState.SUCCESS);
    assertThat(userCourse.getCourse().getSuccessCnt()).isEqualTo(1L);
    assertThat(response.getIsSolved()).isTrue();
    assertThat(response.getIsCourseDone()).isTrue();
    assertThat(response.getCoinResponseDtos()).containsExactlyElementsOf(rewards);

    verify(userService).updateUserBaekjoonRank(user);
    verify(coinService).rewardUser(user, ucp);
    verify(userCourseProblemService).getByUserCourse(userCourse);
    verify(solvedCheckingService).isProblemSolved(user.getBaekjoonId(), problem, ucp);
  }

  @Test
  @DisplayName("checkProblemSolved: DEADLINE 코스 일반 문제 정상 처리 및 다음 문제 활성화")
  void checkProblemSolvedDeadlineGeneral() {
    // given
    User user = TestFixture.getMockUserByOauthId(oauthId);
    Problem problem = TestFixture.getMockProblem(problemId, "문제", 1);
    UserCourse userCourse = TestFixture.getMockUserCourse(user, CourseType.DEADLINE, 3, 3);
    UserCourseProblem ucpCurrent =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem, UserCourseProblemStatus.UNSOLVED, null, 1);
    UserCourseProblem ucpNext =
        TestFixture.getMockUserCourseProblem(
            userCourse,
            TestFixture.getMockProblem(2, "다음문제", 1),
            UserCourseProblemStatus.HIDDEN,
            null,
            2);
    List<CoinResponseDto> rewards =
        List.of(CoinResponseDto.of(0, 10, CoinType.SOLVE_REWARD, "solve"));

    given(userService.getUser(oauthId)).willReturn(user);
    given(problemService.getProblemByProblemId(problemId)).willReturn(problem);
    given(userCourseProblemService.getUserCourseProblemByProblem(problem, user))
        .willReturn(ucpCurrent);
    given(solvedCheckingService.isProblemSolved(user.getBaekjoonId(), problem, ucpCurrent))
        .willReturn(true);
    given(coinService.rewardUser(user, ucpCurrent)).willReturn(rewards);
    given(userCourseProblemService.getByUserCourse(userCourse))
        .willReturn(List.of(ucpCurrent, ucpNext));

    // when
    ProblemSolvedResponseDto response = problemFacade.checkProblemSolved(problemId, oauthId);

    // then
    assertThat(ucpCurrent.getUserCourseProblemStatus()).isEqualTo(UserCourseProblemStatus.SOLVED);
    assertThat(user.getSolvedCount()).isEqualTo(1);
    assertThat(ucpNext.getUserCourseProblemStatus()).isEqualTo(UserCourseProblemStatus.UNSOLVED);
    assertThat(userCourse.getUserCourseState()).isEqualTo(UserCourseState.IN_PROGRESS);
    assertThat(userCourse.getCourse().getSuccessCnt()).isZero();
    assertThat(response.getIsSolved()).isTrue();
    assertThat(response.getIsCourseDone()).isFalse();
    assertThat(response.getCoinResponseDtos()).containsExactlyElementsOf(rewards);

    verify(userService).updateUserBaekjoonRank(user);
    verify(coinService).rewardUser(user, ucpCurrent);
    verify(userCourseProblemService).getByUserCourse(userCourse);
    verify(solvedCheckingService).isProblemSolved(user.getBaekjoonId(), problem, ucpCurrent);
  }

  @Test
  @DisplayName("checkProblemSolved: DAILY 코스 마지막 문제 정상 처리 및 COURSE_REWARD 포함")
  void checkProblemSolvedDailyLast() {
    // given
    User user = TestFixture.getMockUserByOauthId(oauthId);
    Problem problem = TestFixture.getMockProblem(problemId, "제목", 1);
    UserCourse userCourse = TestFixture.getMockUserCourse(user, CourseType.DAILY, 3, 3);
    UserCourseProblem ucp =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem, UserCourseProblemStatus.UNSOLVED, null, 3);
    List<CoinResponseDto> rewards =
        List.of(
            CoinResponseDto.of(0, 10, CoinType.SOLVE_REWARD, "solve"),
            CoinResponseDto.of(10, 50, CoinType.COURSE_REWARD, "course complete"));

    given(userService.getUser(oauthId)).willReturn(user);
    given(problemService.getProblemByProblemId(problemId)).willReturn(problem);
    given(userCourseProblemService.getUserCourseProblemByProblem(problem, user)).willReturn(ucp);
    given(solvedCheckingService.isProblemSolved(user.getBaekjoonId(), problem, ucp))
        .willReturn(true);
    given(coinService.rewardUser(user, ucp)).willReturn(rewards);
    given(userCourseProblemService.getByUserCourse(userCourse)).willReturn(List.of(ucp));

    // when
    ProblemSolvedResponseDto response = problemFacade.checkProblemSolved(problemId, oauthId);

    // then
    assertThat(ucp.getUserCourseProblemStatus()).isEqualTo(UserCourseProblemStatus.SOLVED);
    assertThat(user.getSolvedCount()).isEqualTo(1);
    assertThat(userCourse.getUserCourseState()).isEqualTo(UserCourseState.SUCCESS);
    assertThat(userCourse.getCourse().getSuccessCnt()).isEqualTo(1L);
    assertThat(response.getIsSolved()).isTrue();
    assertThat(response.getIsCourseDone()).isTrue();
    assertThat(response.getCoinResponseDtos()).containsExactlyElementsOf(rewards);

    verify(userService).updateUserBaekjoonRank(user);
    verify(coinService).rewardUser(user, ucp);
    verify(userCourseProblemService).getByUserCourse(userCourse);
    verify(solvedCheckingService).isProblemSolved(user.getBaekjoonId(), problem, ucp);
  }

  @Test
  @DisplayName("checkProblemSolved: DAILY 코스 일반 문제 정상 처리 및 다음 문제 미활성화")
  void checkProblemSolvedDailyGeneral() {
    // given
    User user = TestFixture.getMockUserByOauthId(oauthId);
    Problem problem = TestFixture.getMockProblem(problemId, "제목", 1);
    UserCourse userCourse = TestFixture.getMockUserCourse(user, CourseType.DAILY, 3, 3);
    UserCourseProblem ucpCurrent =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem, UserCourseProblemStatus.UNSOLVED, null, 1);
    UserCourseProblem ucpNext =
        TestFixture.getMockUserCourseProblem(
            userCourse,
            TestFixture.getMockProblem(2, "next", 1),
            UserCourseProblemStatus.HIDDEN,
            null,
            2);
    List<CoinResponseDto> rewards =
        List.of(CoinResponseDto.of(0, 10, CoinType.SOLVE_REWARD, "solve"));

    given(userService.getUser(oauthId)).willReturn(user);
    given(problemService.getProblemByProblemId(problemId)).willReturn(problem);
    given(userCourseProblemService.getUserCourseProblemByProblem(problem, user))
        .willReturn(ucpCurrent);
    given(solvedCheckingService.isProblemSolved(user.getBaekjoonId(), problem, ucpCurrent))
        .willReturn(true);
    given(coinService.rewardUser(user, ucpCurrent)).willReturn(rewards);
    given(userCourseProblemService.getByUserCourse(userCourse))
        .willReturn(List.of(ucpCurrent, ucpNext));

    // when
    ProblemSolvedResponseDto response = problemFacade.checkProblemSolved(problemId, oauthId);

    // then
    assertThat(ucpCurrent.getUserCourseProblemStatus()).isEqualTo(UserCourseProblemStatus.SOLVED);
    assertThat(user.getSolvedCount()).isEqualTo(1);
    assertThat(ucpNext.getUserCourseProblemStatus()).isEqualTo(UserCourseProblemStatus.HIDDEN);
    assertThat(userCourse.getUserCourseState()).isEqualTo(UserCourseState.IN_PROGRESS);
    assertThat(userCourse.getCourse().getSuccessCnt()).isZero();
    assertThat(response.getIsSolved()).isTrue();
    assertThat(response.getIsCourseDone()).isFalse();
    assertThat(response.getCoinResponseDtos()).containsExactlyElementsOf(rewards);

    verify(userService).updateUserBaekjoonRank(user);
    verify(coinService).rewardUser(user, ucpCurrent);
    verify(userCourseProblemService).getByUserCourse(userCourse);
    verify(solvedCheckingService).isProblemSolved(user.getBaekjoonId(), problem, ucpCurrent);
  }
}
