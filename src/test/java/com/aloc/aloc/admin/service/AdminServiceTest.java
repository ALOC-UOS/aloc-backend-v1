package com.aloc.aloc.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.aloc.aloc.report.dto.response.ReportResponseDto;
import com.aloc.aloc.report.service.ReportService;
import com.aloc.aloc.user.service.UserService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

  @Mock private UserService userService;
  @Mock private ReportService reportService;

  @InjectMocks private AdminService adminService;

  @Test
  @DisplayName("관리자 모든 문의사항 조회 성공")
  void getAllReportsSuccess() {
    // given
    String adminOauthId = "admin_123";
    List<ReportResponseDto> expectedReports =
        List.of(ReportResponseDto.builder().id(1L).title("테스트 문의").content("테스트 내용").build());

    willDoNothing().given(userService).validateAdmin(adminOauthId);
    given(reportService.getAllReports()).willReturn(expectedReports);

    // when
    List<ReportResponseDto> result = adminService.getAllReports(adminOauthId);

    // then
    assertThat(result).isEqualTo(expectedReports);
    then(userService).should().validateAdmin(adminOauthId);
    then(reportService).should().getAllReports();
  }

  @Test
  @DisplayName("관리자 문의사항 답변 성공")
  void answerReportSuccess() {
    // given
    Long reportId = 1L;
    String responderUsername = "admin_123";
    String response = "답변 내용입니다.";
    String expectedResult = "답변이 성공적으로 등록되었습니다.";

    willDoNothing().given(userService).validateAdmin(responderUsername);
    given(reportService.answerReport(reportId, responderUsername, response))
        .willReturn(expectedResult);

    // when
    String result = adminService.answerReport(reportId, responderUsername, response);

    // then
    assertThat(result).isEqualTo(expectedResult);
    then(userService).should().validateAdmin(responderUsername);
    then(reportService).should().answerReport(reportId, responderUsername, response);
  }

  @Test
  @DisplayName("관리자가 아닌 사용자 문의사항 조회 실패")
  void getAllReportsNotAdminFail() {
    // given
    String nonAdminOauthId = "user_123";

    willThrow(new IllegalArgumentException("관리자가 아닙니다."))
        .given(userService)
        .validateAdmin(nonAdminOauthId);

    // when & then
    assertThatThrownBy(() -> adminService.getAllReports(nonAdminOauthId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("관리자가 아닙니다.");

    then(reportService).should(never()).getAllReports();
  }

  @Test
  @DisplayName("관리자가 아닌 사용자 문의사항 답변 실패")
  void answerReportNotAdminFail() {
    // given
    Long reportId = 1L;
    String nonAdminUsername = "user_123";
    String response = "답변 내용";

    willThrow(new IllegalArgumentException("관리자가 아닙니다."))
        .given(userService)
        .validateAdmin(nonAdminUsername);

    // when & then
    assertThatThrownBy(() -> adminService.answerReport(reportId, nonAdminUsername, response))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("관리자가 아닙니다.");

    then(reportService).should(never()).answerReport(any(), any(), any());
  }
}
