package com.aloc.aloc.problem.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.user.entity.User;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ProblemServiceTest {

  @InjectMocks @Spy private ProblemService problemService;

  @Mock private ProblemRepository problemRepository;

  private List<Problem> problems;
  private List<ProblemResponseDto> problemResponseDtos;
  private ProblemType problemType;
  private User user1;
  private User user2;

  @BeforeEach
  void init() {
    ReflectionTestUtils.setField(problemService, "currentSeason", 1);

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
    user2 =
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

    problemType = ProblemType.builder().course(Course.FULL).routine(Routine.DAILY).build();
    problemType.setId(1L);

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
    problemResponseDtos =
        Arrays.asList(
            ProblemResponseDto.builder().problemId(1).title("Problem 1").build(),
            ProblemResponseDto.builder().problemId(2).title("Problem 2").build());
  }

  @Test
  @DisplayName("문제 목록 조회 실패 테스트")
  void getSolvedUserListByInvalidProblemIdShouldThrowIllegalArgumentException() {
    // Given
    Long problemId = 1L;
    // When
    when(problemRepository.findById(problemId)).thenReturn(Optional.empty());
    // Then
    assertThrows(IllegalArgumentException.class, () -> problemService.checkProblemExist(problemId));
  }
}
