package com.aloc.aloc.report.service.facade;

import com.aloc.aloc.report.dto.request.ReportRequestDto;
import com.aloc.aloc.report.dto.response.ReportResponseDto;
import com.aloc.aloc.report.service.ReportService;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportFacade {

  private final ReportService reportService;
  private final UserService userService;

  @Transactional
  public String createReport(String username, ReportRequestDto reportRequestDto) {
    User user = userService.getUser(username);
    return reportService.createReportWithUser(user, reportRequestDto);
  }

  @Transactional(readOnly = true)
  public List<ReportResponseDto> getUserReports(String username) {
    User user = userService.getUser(username);
    return reportService.getUserReportsWithUser(user);
  }

  @Transactional
  public String deleteReport(Long reportId, String username) {
    User user = userService.getUser(username);
    return reportService.deleteReportWithUser(reportId, user);
  }

  @Transactional
  public String answerReport(Long reportId, String responderUsername, String responseContent) {
    User responder = userService.getUser(responderUsername);
    return reportService.answerReportWithUser(reportId, responder, responseContent);
  }
}
