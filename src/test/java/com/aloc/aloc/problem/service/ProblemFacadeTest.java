package com.aloc.aloc.problem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problem.enums.UserProblemStatus;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.service.UserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProblemFacadeTest {
  @Spy @InjectMocks private ProblemFacade problemFacade;

  @Mock private ProblemService problemService;

  @Mock private UserService userService;

  @Mock private ProblemSolvingService problemSolvingService;

  @Mock private ProblemMapper problemMapper;

  private List<UserProblem> user1SolvedProblems;
  private List<Problem> problems;
  private List<SolvedUserResponseDto> solvedUsers;
  private List<UserProblem> solvedProblems;
  private User user1;

  @BeforeEach
  void setUp() {
    // Set up Users
    user1 =
        new User(
            "user1",
            "baekjoon1",
            "github1",
            "20210001",
            "password",
            "discord",
            15,
            "notion",
            Course.FULL,
            "oauthId",
            "userId",
            "nickname",
            "profileImageUrl");
    user1.setId(1L);
    user1.setAuthority(Authority.ROLE_USER);
    User user2 =
        new User(
            "user2",
            "baekjoon2",
            "github2",
            "20210002",
            "password",
            "discord",
            15,
            "notion",
            Course.FULL,
            "oauthId",
            "userId",
            "nickname",
            "profileImageUrl");
    user2.setId(2L);

    // Set up Algorithms
    Algorithm algorithm1 = new Algorithm(1, 1, "Algorithm 1", 2, null);
    Algorithm algorithm2 = new Algorithm(2, 2, "Algorithm 2", 2, null);

    // Set up Problems
    Problem problem1 = new Problem("Problem 1", 3, algorithm1, null, null);
    Problem problem2 = new Problem("Problem 2", 4, algorithm2, null, null);
    problem1.setId(1L);
    problem2.setId(2L);
    problems = Arrays.asList(problem1, problem2);

    // Set up ProblemResponseDtos
    List<ProblemResponseDto> problemResponseDtos =
        Arrays.asList(
            ProblemResponseDto.builder().problemId(1).title("Problem 1").build(),
            ProblemResponseDto.builder().problemId(2).title("Problem 2").build());

    // Set up SolvedUserResponseDtos
    solvedUsers =
        Arrays.asList(
            SolvedUserResponseDto.builder()
                .username("user1")
                .githubId("github1")
                .baekjoonId("baekjoon1")
                .profileColor("blue")
                .studentId("20210001")
                .rank(10)
                .coin(100)
                .solvedAt("11:30:00")
                .build(),
            SolvedUserResponseDto.builder()
                .username("user2")
                .githubId("github2")
                .baekjoonId("baekjoon2")
                .profileColor("red")
                .studentId("20210002")
                .rank(20)
                .coin(200)
                .solvedAt("12:45:00")
                .build());

    // Set up SolvedProblem
    solvedProblems =
        Arrays.asList(
            UserProblem.builder()
                .user(user1)
                .problem(problem1)
                .season(2)
                .userProblemStatus(UserProblemStatus.SOLVED)
                .build(),
            UserProblem.builder()
                .user(user2)
                .problem(problem1)
                .season(2)
                .userProblemStatus(UserProblemStatus.SOLVED)
                .build());

    user1SolvedProblems =
        Arrays.asList(
            UserProblem.builder()
                .user(user1)
                .problem(problem1)
                .season(2)
                .userProblemStatus(UserProblemStatus.SOLVED)
                .build(),
            UserProblem.builder()
                .user(user1)
                .problem(problem2)
                .season(2)
                .userProblemStatus(UserProblemStatus.SOLVED)
                .build());
  }

  @Test
  @DisplayName("문제 푼 유저 목록 조회 테스트")
  void getSolvedUserListByProblemIdShouldReturnListOfSolvedUserResponseDto() {
    // Given
    Long problemId = 1L;
    List<SolvedUserResponseDto> expectedUsers = solvedUsers;
    when(problemSolvingService.getSolvedUserListByProblemId(problemId)).thenReturn(solvedProblems);

    // problemMapper.mapToSolvedUserResponseDto() 모킹 추가
    for (int i = 0; i < solvedProblems.size(); i++) {
      when(problemMapper.mapToSolvedUserResponseDto(
              solvedProblems.get(i).getUser(), solvedProblems.get(i)))
          .thenReturn(expectedUsers.get(i));
    }

    // When
    List<SolvedUserResponseDto> actualUsers = problemFacade.getSolvedUserListByProblemId(problemId);

    // Then
    assertEquals(expectedUsers, actualUsers);
  }

  @Test
  @DisplayName("유저가 푼 문제 목록 조회 성공 테스트")
  void getSolvedProblemListByUserShouldReturnListOfSolvedProblem() {
    // Given=
    when(userService.findUser(user1.getGithubId())).thenReturn(user1); // 추가된 부분
    when(problemSolvingService.getSolvedProblemListByUser(user1, 2))
        .thenReturn(
            Arrays.asList(
                ProblemSolvedResponseDto.builder()
                    .id(1L)
                    .problemId(222)
                    .problemTitle("Problem 1")
                    .isSolved(true)
                    .problemDifficulty(3)
                    .build(),
                ProblemSolvedResponseDto.builder()
                    .id(2L)
                    .problemId(2224)
                    .problemTitle("Problem 2")
                    .isSolved(true)
                    .problemDifficulty(4)
                    .build()));

    // When
    List<ProblemSolvedResponseDto> result =
        problemFacade.getSolvedProblemListByUser(user1.getGithubId(), 2);

    // Then
    assertEquals(2, result.size());
    verify(userService).findUser(user1.getGithubId()); // 추가된 부분
  }

  @Test
  @DisplayName("유저가 푼 문제 목록 조회 실패 테스트")
  void checkSolvedLoadUserProblemRecordThrowsExceptionPropagatesException() {
    // Arrange
    String username = "testUser";
    when(userService.getActiveUser(username)).thenReturn(user1);
    doThrow(new RuntimeException("Error loading problem record"))
        .when(problemFacade)
        .loadUserProblemRecord(user1);

    // Act & Assert
    assertThrows(RuntimeException.class, () -> problemFacade.checkSolved(username));
    verify(userService).getActiveUser(username);
    verify(problemFacade).loadUserProblemRecord(user1);
  }

  @Test
  @DisplayName("유저 문제 기록 로드 성공 테스트")
  void loadUserProblemRecordWithProblemsUpdatesEachProblem() {
    // Arrange
    List<Problem> mockProblems = problems;
    Problem todayProblem = problems.get(0);
    ProblemResponseDto todayProblemDto = new ProblemResponseDto();
    todayProblemDto.setProblemId(todayProblem.getProblemId());

    when(problemService.getVisibleProblemsBySeasonAndCourse(user1.getCourse()))
        .thenReturn(mockProblems);

    // Act
    problemFacade.loadUserProblemRecord(user1);

    // Assert
    verify(problemService).getVisibleProblemsBySeasonAndCourse(user1.getCourse());
    for (Problem problem : mockProblems) {
      verify(problemSolvingService).updateUserAndSaveSolvedProblem(user1, problem);
    }
  }

  @Test
  @DisplayName("유저 문제 기록 로드 성공 테스트 - 문제가 많은 경우")
  void loadUserProblemRecordWithManyProblemsHandlesLargeNumber() {
    // Arrange
    List<Problem> mockProblems = createManyMockProblems();

    Problem todayProblem = problems.get(0);
    ProblemResponseDto todayProblemDto = new ProblemResponseDto();
    todayProblemDto.setProblemId(todayProblem.getProblemId());

    when(problemService.getVisibleProblemsBySeasonAndCourse(user1.getCourse()))
        .thenReturn(mockProblems);

    // Act
    problemFacade.loadUserProblemRecord(user1);

    // Assert
    verify(problemSolvingService, times(1000))
        .updateUserAndSaveSolvedProblem(eq(user1), any(Problem.class));
  }

  @Test
  @DisplayName("위클리 문제 가져오기 성공 테스트")
  void getWeeklyProblemSuccess() {
    // Arrange
    when(userService.findUser(user1.getGithubId())).thenReturn(user1);
    List<ProblemSolvedResponseDto> mockProblems =
        Arrays.asList(
            ProblemSolvedResponseDto.builder()
                .id(1L)
                .problemId(222)
                .problemTitle("Problem 1")
                .isSolved(true)
                .problemDifficulty(3)
                .build(),
            ProblemSolvedResponseDto.builder()
                .id(2L)
                .problemId(2224)
                .problemTitle("Problem 2")
                .isSolved(false)
                .problemDifficulty(4)
                .build());
    when(problemSolvingService.getWeeklyProblems(user1)).thenReturn(mockProblems);

    // Act
    List<ProblemSolvedResponseDto> result = problemFacade.getWeeklyProblems(user1.getGithubId());

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.get(0).getIsSolved());
    assertFalse(result.get(1).getIsSolved());
    assertEquals(222, result.get(0).getProblemId());
    assertEquals(4, result.get(1).getProblemDifficulty());
    verify(userService).findUser(user1.getGithubId());
    verify(problemSolvingService).getWeeklyProblems(user1);
  }

  private Problem createMockProblem(Long id) {
    Problem problem = new Problem();
    problem.setId(id);
    return problem;
  }

  private List<Problem> createManyMockProblems() {
    List<Problem> problems = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      problems.add(createMockProblem((long) i));
    }
    return problems;
  }
}
