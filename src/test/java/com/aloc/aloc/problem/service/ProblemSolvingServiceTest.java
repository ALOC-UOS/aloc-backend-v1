package com.aloc.aloc.problem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.entity.User;
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
public class ProblemSolvingServiceTest {
  @Mock private ProblemService problemService;

  @Mock private UserProblemService userProblemService;

  @Mock private ProblemMapper problemMapper;

  @Spy @InjectMocks private ProblemSolvingService problemSolvingService;

  private User user;
  private Problem problem1;
  private Problem problem2;
  private List<Problem> mockWeeklyProblems;
  private ProblemSolvedResponseDto dto1;
  private ProblemSolvedResponseDto dto2;

  @BeforeEach
  void setUp() {
    user =
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

    Algorithm algorithm1 = new Algorithm(1, 1, "Algorithm 1", 2, false);
    Algorithm algorithm2 = new Algorithm(2, 2, "Algorithm 2", 2, true);
    problem1 = new Problem("Problem 1", 3, algorithm2, null, null);
    problem2 = new Problem("Problem 2", 4, algorithm2, null, null);
    problem1.setId(1L);
    problem2.setId(2L);
    mockWeeklyProblems = Arrays.asList(problem1, problem2);

    dto1 =
        ProblemSolvedResponseDto.builder()
            .id(problem1.getId())
            .problemTitle(problem1.getTitle())
            .problemId(problem1.getProblemId())
            .isSolved(true)
            .build();
    dto2 =
        ProblemSolvedResponseDto.builder()
            .id(problem2.getId())
            .problemTitle(problem2.getTitle())
            .problemId(problem2.getProblemId())
            .isSolved(false)
            .build();
  }

  @Test
  @DisplayName("주간 문제 목록 조회 테스트")
  public void testGetWeeklyProblems() {
    // Given
    when(problemService.getWeeklyProblems(user)).thenReturn(mockWeeklyProblems);
    when(userProblemService.isProblemAlreadySolved(user.getId(), problem1.getId()))
        .thenReturn(true);
    when(userProblemService.isProblemAlreadySolved(user.getId(), problem2.getId()))
        .thenReturn(false);
    when(problemMapper.mapToProblemSolvedResponseDto(problem1, true)).thenReturn(dto1);
    when(problemMapper.mapToProblemSolvedResponseDto(problem2, false)).thenReturn(dto2);

    // When
    List<ProblemSolvedResponseDto> result = problemSolvingService.getWeeklyProblems(user);
    // Then
    assertNotNull(result);
    assertEquals(2, result.size());

    assertEquals(dto1, result.get(0));
    assertEquals(dto2, result.get(1));

    // Verify method calls
    verify(problemService).getWeeklyProblems(user);
    verify(userProblemService).isProblemAlreadySolved(eq(user.getId()), eq(1L));
    verify(userProblemService).isProblemAlreadySolved(eq(user.getId()), eq(2L));
    verify(problemMapper, times(2)).mapToProblemSolvedResponseDto(any(Problem.class), anyBoolean());
  }
}
