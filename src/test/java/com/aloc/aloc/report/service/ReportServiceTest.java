package com.aloc.aloc.report.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.report.dto.request.ReportRequestDto;
import com.aloc.aloc.report.dto.response.ReportResponseDto;
import com.aloc.aloc.report.entity.Report;
import com.aloc.aloc.report.enums.ReportState;
import com.aloc.aloc.report.enums.ReportType;
import com.aloc.aloc.report.repository.ReportRepository;
import com.aloc.aloc.user.entity.User;
import java.util.List;
import java.util.NoSuchElementException;
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

  @InjectMocks private ReportService reportService;

  @Test
  @DisplayName("문의사항 생성 성공")
  void createReportWithUserSuccess() {
    // given
    User mockUser = TestFixture.getMockUserByOauthId("test_user");
    ReportRequestDto requestDto = TestFixture.getMockReportRequestDto();
    Report mockReport =
        TestFixture.getMockReport(
            mockUser, requestDto.getReportType(), requestDto.getTitle(), requestDto.getContent());

    given(reportRepository.save(any(Report.class))).willReturn(mockReport);

    // when
    String result = reportService.createReportWithUser(mockUser, requestDto);

    // then
    assertThat(result).isEqualTo("문의사항이 성공적으로 등록되었습니다.");
    then(reportRepository).should().save(any(Report.class));
  }

  @Test
  @DisplayName("모든 문의사항 조회 성공")
  void getAllReportsSuccess() {
    // given
    User user = TestFixture.getMockUserByOauthId("test_user");
    List<Report> mockReports =
        List.of(
            TestFixture.getMockReport(user, ReportType.BUG, "버그 신고", "버그 내용"),
            TestFixture.getMockReport(user, ReportType.FEATURE_REQUEST, "기능 요청", "기능 내용"));

    given(reportRepository.findAllExceptDeleted(ReportState.DELETED)).willReturn(mockReports);

    // when
    List<ReportResponseDto> result = reportService.getAllReports();

    // then
    assertThat(result).hasSize(2);
    then(reportRepository).should().findAllExceptDeleted(ReportState.DELETED);
  }

  @Test
  @DisplayName("사용자 문의사항 조회 성공")
  void getUserReportsWithUserSuccess() {
    // given
    User mockUser = TestFixture.getMockUserByOauthId("test_user");
    List<Report> mockReports =
        List.of(TestFixture.getMockReport(mockUser, ReportType.QUESTION, "질문", "질문 내용"));

    given(reportRepository.findAllByUserExceptDeleted(mockUser, ReportState.DELETED))
        .willReturn(mockReports);

    // when
    List<ReportResponseDto> result = reportService.getUserReportsWithUser(mockUser);

    // then
    assertThat(result).hasSize(1);
    then(reportRepository).should().findAllByUserExceptDeleted(mockUser, ReportState.DELETED);
  }

  @Test
  @DisplayName("문의사항 답변 성공")
  void answerReportWithUserSuccess() {
    // given
    Long reportId = 1L;
    User mockResponder = TestFixture.getMockAdminUser();
    String responseContent = "답변 내용입니다.";
    User requester = TestFixture.getMockUserByOauthId("test_user");
    Report mockReport = TestFixture.getMockReport(requester, ReportType.BUG, "버그 신고", "버그 내용");

    given(reportRepository.findByIdExceptDeleted(reportId, ReportState.DELETED))
        .willReturn(Optional.of(mockReport));

    // when
    String result = reportService.answerReportWithUser(reportId, mockResponder, responseContent);

    // then
    assertThat(result).isEqualTo("답변이 성공적으로 등록되었습니다.");
    then(reportRepository).should().findByIdExceptDeleted(reportId, ReportState.DELETED);
  }

  @Test
  @DisplayName("존재하지 않는 문의사항 답변 실패")
  void answerNonExistentReportFail() {
    // given
    Long reportId = 999L;
    User mockResponder = TestFixture.getMockAdminUser();
    String responseContent = "답변 내용";

    given(reportRepository.findByIdExceptDeleted(reportId, ReportState.DELETED))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(
            () -> reportService.answerReportWithUser(reportId, mockResponder, responseContent))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage("해당 문의사항을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("문의사항 삭제 성공")
  void deleteReportWithUserSuccess() {
    // given
    Long reportId = 1L;
    User mockUser = TestFixture.getMockUserByOauthId("test_user");
    Report mockReport = TestFixture.getMockReport(mockUser, ReportType.BUG, "버그 신고", "버그 내용");

    given(reportRepository.findByIdAndUserExceptDeleted(reportId, mockUser, ReportState.DELETED))
        .willReturn(Optional.of(mockReport));

    // when
    String result = reportService.deleteReportWithUser(reportId, mockUser);

    // then
    assertThat(result).isEqualTo("문의사항이 삭제되었습니다.");
    assertThat(mockReport.getReportState()).isEqualTo(ReportState.DELETED);
  }

  @Test
  @DisplayName("이미 삭제된 문의사항 삭제 실패")
  void deleteAlreadyDeletedReportFail() {
    // given
    Long reportId = 1L;
    User mockUser = TestFixture.getMockUserByOauthId("test_user");
    Report mockReport = TestFixture.getMockReport(mockUser, ReportType.BUG, "버그 신고", "버그 내용");
    mockReport.updateReportState(ReportState.DELETED);

    given(reportRepository.findByIdAndUserExceptDeleted(reportId, mockUser, ReportState.DELETED))
        .willReturn(Optional.of(mockReport));

    // when & then
    assertThatThrownBy(() -> reportService.deleteReportWithUser(reportId, mockUser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이미 삭제된 문의사항입니다.");
  }

  @Test
  @DisplayName("대기 중인 문의사항 개수 조회 성공")
  void countWaitingReportsSuccess() {
    // given
    long expectedCount = 3L;
    given(reportRepository.countByReportState(ReportState.WAITING)).willReturn(expectedCount);

    // when
    long result = reportService.countWaitingReports();

    // then
    assertThat(result).isEqualTo(expectedCount);
    then(reportRepository).should().countByReportState(ReportState.WAITING);
  }
}
