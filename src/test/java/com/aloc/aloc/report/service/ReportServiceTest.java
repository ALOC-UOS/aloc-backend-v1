package com.aloc.aloc.report.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.report.dto.request.ReportRequestDto;
import com.aloc.aloc.report.dto.response.ReportResponseDto;
import com.aloc.aloc.report.entity.Report;
import com.aloc.aloc.report.enums.ReportState;
import com.aloc.aloc.report.enums.ReportType;
import com.aloc.aloc.report.repository.ReportRepository;
import com.aloc.aloc.report.service.facade.ReportFacade;
import com.aloc.aloc.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

  @Mock private ReportRepository reportRepository;
  @Mock private ReportFacade reportFacade;

  @InjectMocks private ReportService reportService;

  @Test
  @DisplayName("문의사항 생성 성공")
  void createReportSuccess() {
    // given
    String username = "test_user";
    User user = TestFixture.getMockUserByOauthId(username);

    // ReportRequestDto Mock 생성
    ReportRequestDto requestDto = mock(ReportRequestDto.class);
    given(requestDto.getReportType()).willReturn(ReportType.BUG);
    given(requestDto.getTitle()).willReturn("테스트 문의");
    given(requestDto.getContent()).willReturn("테스트 내용");
    given(requestDto.getIsPublic()).willReturn(false);

    Report report =
        TestFixture.getMockReport(
            user, requestDto.getReportType(), requestDto.getTitle(), requestDto.getContent());

    given(reportFacade.findUserByUsername(username)).willReturn(user);
    given(reportRepository.save(any(Report.class))).willReturn(report);

    // when
    String result = reportService.createReport(username, requestDto);

    // then
    assertThat(result).isEqualTo("문의사항이 성공적으로 등록되었습니다.");
    then(reportRepository).should().save(any(Report.class));
  }

  @Test
  @DisplayName("모든 문의사항 조회 성공")
  void getAllReportsSuccess() {
    // given
    User user = TestFixture.getMockUserByOauthId("test_user");
    List<Report> reports =
        List.of(
            TestFixture.getMockReport(user, ReportType.BUG, "버그 신고", "버그 내용"),
            TestFixture.getMockReport(user, ReportType.FEATURE_REQUEST, "기능 요청", "기능 내용"));

    given(reportRepository.findAllExceptDeleted(ReportState.DELETED)).willReturn(reports);

    // when
    List<ReportResponseDto> result = reportService.getAllReports();

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getTitle()).isEqualTo("버그 신고");
    assertThat(result.get(1).getTitle()).isEqualTo("기능 요청");
  }

  @Test
  @DisplayName("사용자 문의사항 조회 성공")
  void getUserReportsSuccess() {
    // given
    String username = "test_user";
    User user = TestFixture.getMockUserByOauthId(username);
    List<Report> reports =
        List.of(TestFixture.getMockReport(user, ReportType.QUESTION, "질문", "질문 내용"));

    given(reportFacade.findUserByUsername(username)).willReturn(user);
    given(reportRepository.findAllByUserExceptDeleted(user, ReportState.DELETED))
        .willReturn(reports);

    // when
    List<ReportResponseDto> result = reportService.getUserReports(username);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getTitle()).isEqualTo("질문");
  }

  @Test
  @DisplayName("문의사항 답변 성공")
  void answerReportSuccess() {
    // given
    Long reportId = 1L;
    String responderUsername = "admin_user";
    String responseContent = "답변 내용입니다.";

    User requester = TestFixture.getMockUserByOauthId("test_user");
    User responder = TestFixture.getMockAdminUser();
    Report report = TestFixture.getMockReport(requester, ReportType.BUG, "버그 신고", "버그 내용");

    given(reportRepository.findByIdExceptDeleted(reportId, ReportState.DELETED))
        .willReturn(Optional.of(report));
    given(reportFacade.findUserByUsername(responderUsername)).willReturn(responder);

    // when
    String result = reportService.answerReport(reportId, responderUsername, responseContent);

    // then
    assertThat(result).isEqualTo("답변이 성공적으로 등록되었습니다.");
    assertThat(report.getResponder()).isEqualTo(responder);
    assertThat(report.getResponse()).isEqualTo(responseContent);
    assertThat(report.getReportState()).isEqualTo(ReportState.ANSWERED);
  }

  @Test
  @DisplayName("문의사항 삭제(소프트 딜리트) 성공")
  void deleteReportSuccess() {
    // given
    Long reportId = 1L;
    String username = "test_user";
    User user = TestFixture.getMockUserByOauthId(username);
    Report report = TestFixture.getMockReport(user, ReportType.BUG, "버그 신고", "버그 내용");

    given(reportFacade.findUserByUsername(username)).willReturn(user);
    given(reportRepository.findByIdAndUserExceptDeleted(reportId, user, ReportState.DELETED))
        .willReturn(Optional.of(report));

    // when
    String result = reportService.deleteReport(reportId, username);

    // then
    assertThat(result).isEqualTo("문의사항이 삭제되었습니다.");
    assertThat(report.getReportState()).isEqualTo(ReportState.DELETED);
  }

  @Test
  @DisplayName("이미 삭제된 문의사항 삭제 실패")
  void deleteAlreadyDeletedReportFail() {
    // given
    Long reportId = 1L;
    String username = "test_user";
    User user = TestFixture.getMockUserByOauthId(username);
    Report deletedReport = TestFixture.getMockReport(user, ReportType.BUG, "버그 신고", "버그 내용");
    deletedReport.updateReportState(ReportState.DELETED);

    given(reportFacade.findUserByUsername(username)).willReturn(user);
    given(reportRepository.findByIdAndUserExceptDeleted(reportId, user, ReportState.DELETED))
        .willReturn(Optional.of(deletedReport));

    // when & then
    assertThatThrownBy(() -> reportService.deleteReport(reportId, username))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이미 삭제된 문의사항입니다.");
  }

  @Test
  @DisplayName("대기 중인 문의사항 개수 조회 성공")
  void countWaitingReportsSuccess() {
    // given
    long expectedCount = 5L;
    given(reportRepository.countByReportState(ReportState.WAITING)).willReturn(expectedCount);

    // when
    long result = reportService.countWaitingReports();

    // then
    assertThat(result).isEqualTo(expectedCount);
  }
}
