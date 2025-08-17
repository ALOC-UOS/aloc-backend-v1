package com.aloc.aloc.algorithm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.repository.AlgorithmRepository;
import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.global.apipayload.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// 테스트 함수 목록
// [getAlgorithmsByIds] 메서드 테스트
// [getAlgorithmsByIds] 없는 알고리즘 아이디 조회 테스트
// [findAlgorithmByAlgorithmId] 기존 알고리즘 조회 테스트
// [createAlgorithm] 새 알고리즘 생성 테스트
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
    List<Integer> algorithmIds = List.of(algorithmId);
    // 새로운 findByAlgorithmIdIn 메서드 모킹
    given(algorithmRepository.findByAlgorithmIdIn(algorithmIds)).willReturn(List.of(algorithm));

    // when
    // algorithmId를 List.of(algorithmId)로 넘겨서 조회하고 반환된 객체를 List<Algorithm> result에 저장
    List<Algorithm> result = algorithmService.getAlgorithmsByIds(algorithmIds);

    // then
    // result의 크기가 1이고, 첫 번째 요소의 algorithmId가 1인지 확인
    assertThat(result).containsExactly(algorithm);
    verify(algorithmRepository).findByAlgorithmIdIn(algorithmIds);
  }

  // [getAlgorithmById] 없는 알고리즘 아이디 조회 테스트
  @Test
  void getAlgorithmsByIdNotFound() {
    // given
    // mock에서는 존재하지 않는 아이디를 조회하면 빈 리스트를 반환
    Integer nonExistentId = 999;
    List<Integer> nonExistentIds = List.of(nonExistentId);
    given(algorithmRepository.findByAlgorithmIdIn(nonExistentIds)).willReturn(List.of());

    // when&then
    assertThatThrownBy(() -> algorithmService.getAlgorithmsByIds(nonExistentIds))
        // NotFoundException 예외가 발생하고, 메시지에 "존재하지 않은 알고리즘 아이디가 포함되어 있습니다."가 포함되어 있는지 확인
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("존재하지 않은 알고리즘 아이디가 포함되어 있습니다")
        .hasMessageContaining("[999]");
  }

  // [getAlgorithmsByIds] 여러 ID 조회 테스트
  @Test
  void getAlgorithmsByIdsMultipleIds() {
    // given
    List<Integer> algorithmIds = List.of(1, 2, 3);
    List<Algorithm> algorithms =
        List.of(
            Algorithm.builder().algorithmId(1).koreanName("정렬").englishName("Sort").build(),
            Algorithm.builder().algorithmId(2).koreanName("그래프").englishName("Graph").build(),
            Algorithm.builder()
                .algorithmId(3)
                .koreanName("DP")
                .englishName("Dynamic Programming")
                .build());
    given(algorithmRepository.findByAlgorithmIdIn(algorithmIds)).willReturn(algorithms);

    // when
    List<Algorithm> result = algorithmService.getAlgorithmsByIds(algorithmIds);

    // then
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getAlgorithmId()).isEqualTo(1);
    assertThat(result.get(1).getAlgorithmId()).isEqualTo(2);
    assertThat(result.get(2).getAlgorithmId()).isEqualTo(3);
    verify(algorithmRepository).findByAlgorithmIdIn(algorithmIds);
  }

  // [findAlgorithmByAlgorithmId] 기존 알고리즘 조회 테스트
  @Test
  void findAlgorithmByAlgorithmIdExisting() {
    // given
    Integer algorithmId = 1;
    Algorithm existingAlgorithm = TestFixture.getMockAlgorithm(algorithmId, "정렬", "Sort");

    given(algorithmRepository.findByAlgorithmId(algorithmId))
        .willReturn(Optional.of(existingAlgorithm));

    // when
    Optional<Algorithm> result = algorithmService.findAlgorithmByAlgorithmId(algorithmId);

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(existingAlgorithm);
    verify(algorithmRepository).findByAlgorithmId(algorithmId);
  }

  // [findAlgorithmByAlgorithmId] 존재하지 않는 알고리즘 조회 테스트
  @Test
  void findAlgorithmByAlgorithmIdNotFound() {
    // given
    Integer algorithmId = 999;
    given(algorithmRepository.findByAlgorithmId(algorithmId)).willReturn(Optional.empty());

    // when
    Optional<Algorithm> result = algorithmService.findAlgorithmByAlgorithmId(algorithmId);

    // then
    assertThat(result).isEmpty();
    verify(algorithmRepository).findByAlgorithmId(algorithmId);
  }

  // [createAlgorithm] 새 알고리즘 생성 테스트
  @Test
  void createAlgorithmSuccess() {
    // given
    Integer algorithmId = 2;
    String koreanName = "그래프";
    String englishName = "Graph";

    Algorithm newAlgorithm = TestFixture.getMockAlgorithm(algorithmId, koreanName, englishName);

    // algorithmRepository에서 save 메서드를 호출하면 newAlgorithm 객체를 반환
    given(algorithmRepository.save(any(Algorithm.class))).willReturn(newAlgorithm);

    // when
    Algorithm result = algorithmService.createAlgorithm(algorithmId, koreanName, englishName);

    // then
    assertThat(result).isEqualTo(newAlgorithm);
    assertThat(result.getAlgorithmId()).isEqualTo(algorithmId);
    assertThat(result.getKoreanName()).isEqualTo(koreanName);
    assertThat(result.getEnglishName()).isEqualTo(englishName);
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
    assertThat(result.get(0).getKoreanName()).isEqualTo("정렬");
    assertThat(result.get(0).getEnglishName()).isEqualTo("Sort");
    assertThat(result.get(1).getAlgorithmId()).isEqualTo(2);
    assertThat(result.get(1).getKoreanName()).isEqualTo("그래프");
    assertThat(result.get(1).getEnglishName()).isEqualTo("Graph");
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

  // [통합 테스트] findAlgorithmByAlgorithmId + createAlgorithm 조합 패턴
  @Test
  void findOrCreateAlgorithmPattern() {
    // given
    Integer algorithmId = 3;
    String koreanName = "동적계획법";
    String englishName = "Dynamic Programming";

    Algorithm newAlgorithm = TestFixture.getMockAlgorithm(algorithmId, koreanName, englishName);

    // 첫 번째 호출: 존재하지 않음
    given(algorithmRepository.findByAlgorithmId(algorithmId)).willReturn(Optional.empty());

    // 두 번째 호출: 생성 성공
    given(algorithmRepository.save(any(Algorithm.class))).willReturn(newAlgorithm);

    // when - 분리된 로직 조합
    Optional<Algorithm> existingAlgorithm =
        algorithmService.findAlgorithmByAlgorithmId(algorithmId);
    Algorithm result =
        existingAlgorithm.orElseGet(
            () -> algorithmService.createAlgorithm(algorithmId, koreanName, englishName));

    // then
    assertThat(result).isEqualTo(newAlgorithm);
    assertThat(result.getAlgorithmId()).isEqualTo(algorithmId);
    verify(algorithmRepository).findByAlgorithmId(algorithmId);
    verify(algorithmRepository).save(any(Algorithm.class));
  }
}
