package com.aloc.aloc.admin.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardResponseDto {
  private long totalUserCount;
  private long activeCourseCount;
  private long activeUserCourseCount;
  private long completedCourseCount;
  private long reportedIssueCount;
  private long newReport;

  public static AdminDashboardResponseDto of(
      long totalUserCount,
      long activeCourseCount,
      long activeUserCourseCount,
      long completedCourseCount,
      long reportedIssueCount,
      long newReport) {
    return AdminDashboardResponseDto.builder()
        .totalUserCount(totalUserCount)
        .activeCourseCount(activeCourseCount)
        .activeUserCourseCount(activeUserCourseCount)
        .completedCourseCount(completedCourseCount)
        .reportedIssueCount(reportedIssueCount)
        .newReport(newReport)
        .build();
  }
}
