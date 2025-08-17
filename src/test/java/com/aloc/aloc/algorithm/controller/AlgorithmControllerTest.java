package com.aloc.aloc.algorithm.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.common.fixture.TestFixture;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

// 테스트 함수 목록
// [getAlgorithms] 정상적인 알고리즘 목록 조회 테스트
// [getAlgorithms] 빈 알고리즘 목록 반환 테스트

// WebMvcTest는 스프링 부트 테스트 어노테이션으로 컨트롤러 레이어만 테스트 가능하게 해준다.
@WebMvcTest(AlgorithmController.class)
// AutoConfigureMockMvc는 MockMvc 객체를 자동으로 생성해준다.
@AutoConfigureMockMvc(addFilters = false)
public class AlgorithmControllerTest {

  // 테스트 코드에서 사용할 MockMvc 객체
  @Autowired private MockMvc mockMvc;

  // 테스트 코드에서 사용할 AlgorithmService Mock 객체
  @MockBean private AlgorithmService algorithmService;

  // JpaMetamodelMappingContext는 스프링 부트 테스트 어노테이션으로 자동으로 생성해준다.
  @MockBean private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  void getAlgorithms() throws Exception {
    // given
    List<AlgorithmResponseDto> mockAlgorithms =
        List.of(
            AlgorithmResponseDto.of(TestFixture.getMockAlgorithm(1, "정렬", "Sort")),
            AlgorithmResponseDto.of(TestFixture.getMockAlgorithm(2, "그래프", "Graph")));
    // algorithmService.getAlgorithms() 호출 시 위에서 생성한 algorithms 리스트를 반환하도록 설정
    given(algorithmService.getAlgorithms()).willReturn(mockAlgorithms);

    // when & then
    // /api/algorithms 엔드포인트에 GET 요청을 보내고 응답을 검증
    mockMvc
        .perform(get("/api/algorithms"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").isArray())
        .andExpect(jsonPath("$.result.length()").value(2))
        .andExpect(jsonPath("$.result[0].algorithmId").value(1))
        .andExpect(jsonPath("$.result[0].koreanName").value("정렬"))
        .andExpect(jsonPath("$.result[1].algorithmId").value(2))
        .andExpect(jsonPath("$.result[1].koreanName").value("그래프"));
  }

  // [getAlgorithms] 빈 알고리즘 목록 반환 테스트
  @Test
  void getAlgorithmsEmptyList() throws Exception {
    // given
    // 빈 알고리즘 목록 데이터 준비
    List<AlgorithmResponseDto> emptyAlgorithms = List.of();

    given(algorithmService.getAlgorithms()).willReturn(emptyAlgorithms);

    // when & then
    mockMvc
        .perform(get("/api/algorithms"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").isArray())
        .andExpect(jsonPath("$.result.length()").value(0));
  }
}
