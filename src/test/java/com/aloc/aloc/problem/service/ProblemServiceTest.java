package com.aloc.aloc.problem.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProblemServiceTest {
  @Mock private ProblemRepository problemRepository;

  @InjectMocks private ProblemService problemService;

  @Test
  @DisplayName("findProblemByProblemId: 유효한 problemId로 Optional 반환")
  void findProblemByProblemIdSuccess() {
    // given
    Integer problemId = 100;
    Problem mockProblem = TestFixture.getMockProblem(problemId, "샘플 문제", 1);
    given(problemRepository.findByProblemId(problemId)).willReturn(Optional.of(mockProblem));

    // when
    Optional<Problem> result = problemService.findProblemByProblemId(problemId);

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getProblemId()).isEqualTo(problemId);
  }

  @Test
  @DisplayName("findProblemByProblemId: 유효하지 않은 problemId로 Optional.empty 반환")
  void findProblemByProblemIdFail() {
    // given
    Integer invalidId = 999;
    given(problemRepository.findByProblemId(invalidId)).willReturn(Optional.empty());

    // when
    Optional<Problem> result = problemService.findProblemByProblemId(invalidId);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("getProblemByProblemId: 존재하는 problemId로 Problem 반환")
  void getProblemByProblemIdSuccess() {
    // given
    Integer problemId = 100;
    Problem mockProblem = TestFixture.getMockProblem(problemId, "샘플 문제", 1);
    given(problemRepository.findByProblemId(problemId)).willReturn(Optional.of(mockProblem));

    // when
    Problem result = problemService.getProblemByProblemId(problemId);

    // then
    assertThat(result).isEqualTo(mockProblem);
  }

  @Test
  @DisplayName("getProblemByProblemId: 존재하지 않는 problemId로 예외 발생")
  void getProblemByProblemIdFail() {
    // given
    Integer invalidId = 999;
    given(problemRepository.findByProblemId(invalidId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> problemService.getProblemByProblemId(invalidId))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage("존재하지 않는 문제 입니다.");
  }

  @Test
  @DisplayName("saveProblem: 문제 저장 요청 시 저장된 Problem 반환")
  void saveProblemSuccess() {
    // given
    Integer problemId = 100;
    Problem toSave = TestFixture.getMockProblem(problemId, "샘플 문제", 1);
    given(problemRepository.save(toSave)).willReturn(toSave);

    // when
    Problem result = problemService.saveProblem(toSave);

    // then
    assertThat(result).isEqualTo(toSave);
  }
}
