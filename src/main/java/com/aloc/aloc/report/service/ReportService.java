package com.aloc.aloc.report.service;

import com.aloc.aloc.report.dto.request.ReportRequestDto;
import com.aloc.aloc.report.dto.response.ReportResponseDto;
import com.aloc.aloc.report.entity.Report;
import com.aloc.aloc.report.enums.ReportState;
import com.aloc.aloc.report.repository.ReportRepository;
import com.aloc.aloc.user.entity.User;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;

  @Transactional
  public String createReportWithUser(User user, ReportRequestDto reportRequestDto) {
    Report report = Report.of(user, reportRequestDto);
    reportRepository.save(report);
    return "문의사항이 성공적으로 등록되었습니다.";
  }

  @Transactional(readOnly = true)
  public List<ReportResponseDto> getAllReports() {
    List<Report> reports = reportRepository.findAllExceptDeleted(ReportState.DELETED);
    return reports.stream().map(ReportResponseDto::of).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ReportResponseDto> getUserReportsWithUser(User user) {
    List<Report> reports = reportRepository.findAllByUserExceptDeleted(user, ReportState.DELETED);
    return reports.stream().map(ReportResponseDto::of).collect(Collectors.toList());
  }

  @Transactional
  public String answerReportWithUser(Long reportId, User responder, String responseContent) {
    Report report = findReportById(reportId);
    report.addResponse(responder, responseContent);
    return "답변이 성공적으로 등록되었습니다.";
  }

  @Transactional
  public String deleteReportWithUser(Long reportId, User user) {
    Report report = findReportByIdAndRequester(reportId, user);

    if (report.getReportState() == ReportState.DELETED) {
      throw new IllegalArgumentException("이미 삭제된 문의사항입니다.");
    }

    report.updateReportState(ReportState.DELETED);
    return "문의사항이 삭제되었습니다.";
  }

  @Transactional(readOnly = true)
  public long countWaitingReports() {
    return reportRepository.countByReportState(ReportState.WAITING);
  }

  private Report findReportById(Long reportId) {
    return reportRepository
        .findByIdExceptDeleted(reportId, ReportState.DELETED)
        .orElseThrow(() -> new NoSuchElementException("해당 문의사항을 찾을 수 없습니다."));
  }

  private Report findReportByIdAndRequester(Long reportId, User user) {
    return reportRepository
        .findByIdAndUserExceptDeleted(reportId, user, ReportState.DELETED)
        .orElseThrow(() -> new NoSuchElementException("본인이 작성한 문의사항을 찾을 수 없습니다."));
  }
}
