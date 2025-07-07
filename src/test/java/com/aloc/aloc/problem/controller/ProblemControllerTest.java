package com.aloc.aloc.problem.controller;

import static com.aloc.aloc.common.fixture.TestFixture.getMockProblemSolvedResponseDto;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aloc.aloc.global.apipayload.exception.AlreadySolvedProblemException;
import com.aloc.aloc.global.apipayload.exception.ProblemNotYetSolvedException;
import com.aloc.aloc.problem.service.facade.ProblemFacade;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProblemController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProblemControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockBean private ProblemFacade problemFacade;

  @MockBean private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @WithMockUser
  void checkProblemSolvedForSuccess() throws Exception {
    // given
    Integer problemId = 123;
    given(problemFacade.checkProblemSolved(anyInt(), anyString()))
        .willReturn(getMockProblemSolvedResponseDto());

    // when & then
    mockMvc
        .perform(patch("/api/problems/{problemId}", problemId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.result.isSolved").value(true))
        .andExpect(jsonPath("$.result.isCourseDone").value(true))
        .andExpect(jsonPath("$.result.coinResponseDtos[0].previousCoin").value(100))
        .andExpect(jsonPath("$.result.coinResponseDtos[0].addedCoin").value(10))
        .andExpect(jsonPath("$.result.coinResponseDtos[0].type").value("SOLVE_REWARD"))
        .andExpect(jsonPath("$.result.coinResponseDtos[0].description").value("문제 해결 보상"));
  }

  @Test
  @WithMockUser
  void checkProblemSolvedForAlreadySolved() throws Exception {
    // given
    Integer problemId = 123;
    given(problemFacade.checkProblemSolved(anyInt(), anyString()))
        .willThrow(new AlreadySolvedProblemException("이미 해결한 문제입니다."));

    // when & then
    mockMvc
        .perform(patch("/api/problems/{problemId}", problemId))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.isSuccess").value(false))
        .andExpect(jsonPath("$.result").value("이미 해결한 문제입니다."));
  }

  @Test
  @WithMockUser
  void checkProblemSolvedForNotYetSolved() throws Exception {
    // given
    Integer problemId = 123;
    given(problemFacade.checkProblemSolved(anyInt(), anyString()))
        .willThrow(new ProblemNotYetSolvedException("아직 채점이 완료되지 않았거나 문제를 해결하지 않았습니다."));

    // when & then
    mockMvc
        .perform(patch("/api/problems/{problemId}", problemId))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.isSuccess").value(false))
        .andExpect(jsonPath("$.result").value("아직 채점이 완료되지 않았거나 문제를 해결하지 않았습니다."));
  }

  @Test
  @WithMockUser
  void checkProblemSolvedForNotFound() throws Exception {
    // given
    Integer problemId = 123;
    given(problemFacade.checkProblemSolved(anyInt(), anyString()))
        .willThrow(new NoSuchElementException("해당 문제 또는 사용자 정보를 찾을 수 없음."));

    // when & then
    mockMvc
        .perform(patch("/api/problems/{problemId}", problemId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.isSuccess").value(false))
        .andExpect(jsonPath("$.result").value("해당 문제 또는 사용자 정보를 찾을 수 없음."));
  }
}
