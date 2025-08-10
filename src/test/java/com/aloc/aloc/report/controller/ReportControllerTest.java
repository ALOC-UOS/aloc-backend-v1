package com.aloc.aloc.report.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.report.dto.response.ReportResponseDto;
import com.aloc.aloc.report.enums.ReportState;
import com.aloc.aloc.report.enums.ReportType;
import com.aloc.aloc.report.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private ReportService reportService;

  @MockBean private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @WithMockUser
  @DisplayName("문의사항 생성 성공")
  void createReportSuccess() throws Exception {
    // given
    String expectedResponse = "문의사항이 성공적으로 등록되었습니다.";
    given(reportService.createReport(anyString(), any())).willReturn(expectedResponse);

    String requestBody = objectMapper.writeValueAsString(TestFixture.getMockReportRequestDto());

    // when & then
    mockMvc
        .perform(post("/api/report").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.result").value(expectedResponse));
  }

  @Test
  @WithMockUser
  @DisplayName("사용자 문의사항 조회 성공")
  void getUserReportsSuccess() throws Exception {
    // given
    List<ReportResponseDto> mockReports =
        List.of(
            ReportResponseDto.builder()
                .id(1L)
                .reportType(ReportType.BUG)
                .title("버그 신고")
                .content("버그 내용입니다.")
                .requesterName("테스트 유저")
                .responderName(null)
                .response(null)
                .reportState(ReportState.WAITING)
                .isPublic(false)
                .build(),
            ReportResponseDto.builder()
                .id(2L)
                .reportType(ReportType.FEATURE_REQUEST)
                .title("기능 요청")
                .content("기능 요청 내용입니다.")
                .requesterName("테스트 유저")
                .responderName("관리자")
                .response("검토하겠습니다.")
                .reportState(ReportState.ANSWERED)
                .isPublic(true)
                .build());

    given(reportService.getUserReports(anyString())).willReturn(mockReports);

    // when & then
    mockMvc
        .perform(get("/api/reports"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.result").isArray())
        .andExpect(jsonPath("$.result.length()").value(2))
        .andExpect(jsonPath("$.result[0].id").value(1))
        .andExpect(jsonPath("$.result[0].reportType").value("BUG"))
        .andExpect(jsonPath("$.result[0].title").value("버그 신고"))
        .andExpect(jsonPath("$.result[0].reportState").value("WAITING"))
        .andExpect(jsonPath("$.result[0].isPublic").value(false))
        .andExpect(jsonPath("$.result[1].id").value(2))
        .andExpect(jsonPath("$.result[1].reportType").value("FEATURE_REQUEST"))
        .andExpect(jsonPath("$.result[1].title").value("기능 요청"))
        .andExpect(jsonPath("$.result[1].reportState").value("ANSWERED"))
        .andExpect(jsonPath("$.result[1].isPublic").value(true))
        .andExpect(jsonPath("$.result[1].responderName").value("관리자"));
  }

  @Test
  @WithMockUser
  @DisplayName("문의사항 삭제 성공")
  void deleteReportSuccess() throws Exception {
    // given
    Long reportId = 1L;
    String expectedResponse = "문의사항이 삭제되었습니다.";
    given(reportService.deleteReport(eq(reportId), anyString())).willReturn(expectedResponse);

    // when & then
    mockMvc
        .perform(delete("/api/reports/{reportId}", reportId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.result").value(expectedResponse));
  }

  @Test
  @WithMockUser
  @DisplayName("문의사항 삭제 시 문의사항 없음 예외")
  void deleteReportNotFound() throws Exception {
    // given
    Long reportId = 999L;
    given(reportService.deleteReport(eq(reportId), anyString()))
        .willThrow(new NoSuchElementException("본인이 작성한 문의사항을 찾을 수 없습니다."));

    // when & then
    mockMvc
        .perform(delete("/api/reports/{reportId}", reportId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.isSuccess").value(false))
        .andExpect(jsonPath("$.result").value("본인이 작성한 문의사항을 찾을 수 없습니다."));
  }
}
