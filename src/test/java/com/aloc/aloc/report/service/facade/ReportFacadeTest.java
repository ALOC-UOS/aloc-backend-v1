package com.aloc.aloc.report.service.facade;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.report.dto.request.ReportRequestDto;
import com.aloc.aloc.report.dto.response.ReportResponseDto;
import com.aloc.aloc.report.entity.Report;
import com.aloc.aloc.report.enums.ReportType;
import com.aloc.aloc.report.service.ReportService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportFacadeTest {

  @Mock private ReportService reportService;
  @Mock private UserService userService;

  @InjectMocks private ReportFacade reportFacade;

  @Test
  @DisplayName("문의사항 생성 성공")
  void createReportSuccess() {
    // given
    String username = "test_user";
    ReportRequestDto requestDto = TestFixture.getMockReportRequestDto();
    User mockUser = TestFixture.getMockUserByOauthId(username);
    String expectedResult = "문의사항이 성공적으로 등록되었습니다.";

    given(userService.getUser(username)).willReturn(mockUser);
    given(reportService.createReportWithUser(mockUser, requestDto)).willReturn(expectedResult);

    // when
    String result = reportFacade.createReport(username, requestDto);

    // then
    assertThat(result).isEqualTo(expectedResult);
    then(userService).should().getUser(username);
    then(reportService).should().createReportWithUser(mockUser, requestDto);
  }

  @Test
  @DisplayName("사용자 문의사항 조회 성공")
  void getUserReportsSuccess() {
    // given
    String username = "test_user";
    User mockUser = TestFixture.getMockUserByOauthId(username);
    List<ReportResponseDto> expectedReports = createMockReportResponseList();

    given(userService.getUser(username)).willReturn(mockUser);
    given(reportService.getUserReportsWithUser(mockUser)).willReturn(expectedReports);

    // when
    List<ReportResponseDto> result = reportFacade.getUserReports(username);

    // then
    assertThat(result).isEqualTo(expectedReports);
    then(userService).should().getUser(username);
    then(reportService).should().getUserReportsWithUser(mockUser);
  }

  @Test
  @DisplayName("문의사항 삭제 성공")
  void deleteReportSuccess() {
    // given
    Long reportId = 1L;
    String username = "test_user";
    User mockUser = TestFixture.getMockUserByOauthId(username);
    String expectedResult = "문의사항이 삭제되었습니다.";

    given(userService.getUser(username)).willReturn(mockUser);
    given(reportService.deleteReportWithUser(reportId, mockUser)).willReturn(expectedResult);

    // when
    String result = reportFacade.deleteReport(reportId, username);

    // then
    assertThat(result).isEqualTo(expectedResult);
    then(userService).should().getUser(username);
    then(reportService).should().deleteReportWithUser(reportId, mockUser);
  }

  @Test
  @DisplayName("문의사항 답변 성공")
  void answerReportSuccess() {
    // given
    Long reportId = 1L;
    String responderUsername = "admin_user";
    String responseContent = "답변 내용입니다.";
    User mockResponder = TestFixture.getMockAdminUser();
    String expectedResult = "답변이 성공적으로 등록되었습니다.";

    given(userService.getUser(responderUsername)).willReturn(mockResponder);
    given(reportService.answerReportWithUser(reportId, mockResponder, responseContent))
        .willReturn(expectedResult);

    // when
    String result = reportFacade.answerReport(reportId, responderUsername, responseContent);

    // then
    assertThat(result).isEqualTo(expectedResult);
    then(userService).should().getUser(responderUsername);
    then(reportService).should().answerReportWithUser(reportId, mockResponder, responseContent);
  }

  @Test
  @DisplayName("존재하지 않는 사용자로 문의사항 생성 실패")
  void createReportWithNonExistentUserFail() {
    // given
    String invalidUsername = "invalid_user";
    ReportRequestDto requestDto = TestFixture.getMockReportRequestDto();

    given(userService.getUser(invalidUsername))
        .willThrow(new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

    // when & then
    assertThatThrownBy(() -> reportFacade.createReport(invalidUsername, requestDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("해당 사용자가 존재하지 않습니다.");

    then(reportService).should(never()).createReportWithUser(any(), any());
  }

  private List<ReportResponseDto> createMockReportResponseList() {
    User user1 = TestFixture.getMockUserByOauthId("user1");
    User user2 = TestFixture.getMockUserByOauthId("user2");
    User admin = TestFixture.getMockAdminUser();

    Report report1 = TestFixture.getMockReport(user1, ReportType.BUG, "버그 신고", "버그 내용");
    Report report2 = TestFixture.getMockAnsweredReport(user2, admin, "답변 내용");

    return List.of(ReportResponseDto.of(report1), ReportResponseDto.of(report2));
  }
}
