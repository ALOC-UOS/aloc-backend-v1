package com.aloc.aloc.admin.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aloc.aloc.admin.service.AdminService;
import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.profilebackgroundcolor.service.ProfileBackgroundColorService;
import com.aloc.aloc.report.dto.response.ReportResponseDto;
import com.aloc.aloc.report.enums.ReportState;
import com.aloc.aloc.report.enums.ReportType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminReportControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AdminService adminService;

  @MockBean private ProfileBackgroundColorService profileBackgroundColorService;

  @MockBean private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("관리자 문의사항 조회 성공")
  void getAllReportsSuccess() throws Exception {
    // given
    List<ReportResponseDto> mockReports =
        List.of(
            ReportResponseDto.builder()
                .id(1L)
                .reportType(ReportType.BUG)
                .title("버그 신고")
                .content("버그 내용입니다.")
                .requesterName("사용자1")
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
                .requesterName("사용자2")
                .responderName("관리자")
                .response("검토하겠습니다.")
                .reportState(ReportState.ANSWERED)
                .isPublic(true)
                .build());

    given(adminService.getAllReports(anyString())).willReturn(mockReports);

    // when & then
    mockMvc
        .perform(get("/admin/reports"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.result").isArray())
        .andExpect(jsonPath("$.result.length()").value(2))
        .andExpect(jsonPath("$.result[0].id").value(1))
        .andExpect(jsonPath("$.result[0].reportType").value("BUG"))
        .andExpect(jsonPath("$.result[0].title").value("버그 신고"))
        .andExpect(jsonPath("$.result[0].reportState").value("WAITING"))
        .andExpect(jsonPath("$.result[0].requesterName").value("사용자1"))
        .andExpect(jsonPath("$.result[1].id").value(2))
        .andExpect(jsonPath("$.result[1].reportType").value("FEATURE_REQUEST"))
        .andExpect(jsonPath("$.result[1].reportState").value("ANSWERED"))
        .andExpect(jsonPath("$.result[1].responderName").value("관리자"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("문의사항 답변 등록 성공")
  void answerReportSuccess() throws Exception {
    // given
    Long reportId = 1L;
    String expectedResponse = "답변이 성공적으로 등록되었습니다.";
    given(adminService.answerReport(eq(reportId), anyString(), anyString()))
        .willReturn(expectedResponse);

    String requestBody =
        objectMapper.writeValueAsString(TestFixture.getMockAdminReportRequestDto());

    // when & then
    mockMvc
        .perform(
            put("/admin/reports/{reportId}", reportId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.result").value(expectedResponse));
  }
}
