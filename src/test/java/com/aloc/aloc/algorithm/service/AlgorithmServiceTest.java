package com.aloc.aloc.algorithm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.repository.AlgorithmRepository;
import com.aloc.aloc.common.fixture.TestFixture;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// 테스트 함수 목록
// [getAlgorithmsByIds] 메서드 테스트
// [getAlgorithmsByIds] 없는 알고리즘 아이디 조회 테스트
// [getOrCreateAlgorithm] 기존 알고리즘 조회 테스트
// [getOrCreateAlgorithm] 새 알고리즘 생성 테스트
// [getAlgorithms] 정상 케이스 테스트
// [getAlgorithms] 빈 목록 케이스 테스트

@ExtendWith(MockitoExtension.class)
public class AlgorithmServiceTest {

  // Algorithm Repository를 모방한 가짜 객체 만들기
  @Mock private AlgorithmRepository algorithmRepository;

  // 가짜 객체를 사용하는 AlgorithmService 객체 만들기
  @InjectMocks private AlgorithmService algorithmService;

  // [getAlgorithmsByIds] 메서드 테스트
  @Test
  void getAlgorithmsByIdsNormalCase() {
    // given
    // id 1인 객체를 생성
    Integer algorithmId = 1;
    Algorithm algorithm =
        Algorithm.builder().algorithmId(algorithmId).koreanName("테스트").englishName("test").build();
    // algorithmRepository에서 algorithmId 1인 객체를 조회하면 algorithm 객체를 반환
    given(algorithmRepository.findByAlgorithmId(algorithmId)).willReturn(Optional.of(algorithm));

    // when
    // algorithmId를 List.of(algorithmId)로 넘겨서 조회하고 반환된 객체를 List<Algorithm> result에 저장
    List<Algorithm> result = algorithmService.getAlgorithmsByIds(List.of(algorithmId));

    // then
    // result의 크기가 1이고, 첫 번째 요소의 algorithmId가 1인지 확인
    assertThat(result).containsExactly(algorithm);
  }

  // [getAlgorithmById] 없는 알고리즘 아이디 조회 테스트
  @Test
  void getAlgorithmsByIdNotFound() {
    // given
    // mock에서는 존재하지 않는 아이디를 조회하면 Optional.empty()를 반환
    Integer nonExistentId = 999;
    given(algorithmRepository.findByAlgorithmId(nonExistentId)).willReturn(Optional.empty());

    // when&then
    assertThatThrownBy(() -> algorithmService.getAlgorithmsByIds(List.of(nonExistentId)))
        // NoSuchElementException 예외가 발생하고, 메시지에 "존재하지 않은 알고리즘 아이디가 포함되어 있습니다."가 포함되어 있는지 확인
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("존재하지 않은 알고리즘 아이디가 포함되어 있습니다.");
  }

  // [getOrCreateAlgorithm] 기존 알고리즘 조회 테스트
  @Test
  void getOrCreateAlgorithmExistingAlgorithm() {
    // given
    Integer algorithmId = 1;
    String koreanName = "정렬";
    String englishName = "Sort";

    Algorithm existingAlgorithm = TestFixture.getMockAlgorithm(algorithmId, koreanName, englishName);

    given(algorithmRepository.findByAlgorithmId(algorithmId))
        .willReturn(Optional.of(existingAlgorithm));

    // when
    Algorithm result = algorithmService.getOrCreateAlgorithm(algorithmId, koreanName, englishName);

    // then
    assertThat(result).isEqualTo(existingAlgorithm);
    verify(algorithmRepository, never()).save(any(Algorithm.class)); // save는 호출되지 않음
  }

  // [getOrCreateAlgorithm] 새 알고리즘 생성 테스트
  @Test
  void getOrCreateAlgorithmNewAlgorithm() {
    // given
    Integer algorithmId = 2;
    String koreanName = "그래프";
    String englishName = "Graph";

    Algorithm newAlgorithm = TestFixture.getMockAlgorithm(algorithmId, koreanName, englishName);

    // algorithmRepository에서 algorithmId 2인 객체를 조회하면 Optional.empty()를 반환
    given(algorithmRepository.findByAlgorithmId(algorithmId)).willReturn(Optional.empty());
    // algorithmRepository에서 save 메서드를 호출하면 newAlgorithm 객체를 반환
    given(algorithmRepository.save(any(Algorithm.class))).willReturn(newAlgorithm);

    // when
    Algorithm result = algorithmService.getOrCreateAlgorithm(algorithmId, koreanName, englishName);

    // then
    assertThat(result).isEqualTo(newAlgorithm);
    verify(algorithmRepository).save(any(Algorithm.class)); // save가 호출됨
  }

  // [getAlgorithms] 정상 케이스 테스트
  @Test
  void getAlgorithmsNormalCase() {
    // given
    Algorithm algorithm1 = TestFixture.getMockAlgorithm(1, "정렬", "Sort");
    Algorithm algorithm2 = TestFixture.getMockAlgorithm(2, "그래프", "Graph");

    List<Algorithm> algorithms = List.of(algorithm1, algorithm2);
    given(algorithmRepository.findAll()).willReturn(algorithms);

    // when
    List<AlgorithmResponseDto> result = algorithmService.getAlgorithms();

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getAlgorithmId()).isEqualTo(1);
    assertThat(result.get(0).getName()).isEqualTo("정렬");
    assertThat(result.get(1).getAlgorithmId()).isEqualTo(2);
    assertThat(result.get(1).getName()).isEqualTo("그래프");
    verify(algorithmRepository).findAll();
  }

  // [getAlgorithms] 빈 목록 케이스 테스트
  @Test
  void getAlgorithmsEmptyList() {
    // given
    // algorithmRepository에서 findAll 메서드를 호출하면 빈 리스트를 반환
    given(algorithmRepository.findAll()).willReturn(List.of());

    // when
    // getAlgorithms 메서드를 호출하고 반환된 객체를 List<AlgorithmResponseDto> result에 저장
    List<AlgorithmResponseDto> result = algorithmService.getAlgorithms();

    // then
    // result가 빈 리스트인지 확인
    assertThat(result).isEmpty();
    verify(algorithmRepository).findAll();
  }
}
