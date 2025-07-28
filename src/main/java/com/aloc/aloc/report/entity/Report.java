package com.aloc.aloc.report.entity;

import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.report.dto.request.ReportRequestDto;
import com.aloc.aloc.report.enums.ReportState;
import com.aloc.aloc.report.enums.ReportType;
import com.aloc.aloc.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class Report extends AuditingTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportType reportType;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "requester_id", nullable = false)
  private User requester;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "responder_id")
  private User responder;

  @Column(columnDefinition = "TEXT")
  private String response;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  @Column(nullable = false)
  private ReportState reportState = ReportState.WAITING;

  @Builder.Default
  @Column(nullable = false)
  private Boolean isPublic = false;

  private LocalDateTime respondAt;

  public static Report of(User requester, ReportRequestDto reportRequestDto) {
    return Report.builder()
        .requester(requester)
        .reportType(reportRequestDto.getReportType())
        .title(reportRequestDto.getTitle())
        .content(reportRequestDto.getContent())
        .isPublic(reportRequestDto.getIsPublic())
        .build();
  }

  public void addResponse(User responder, String response) {
    this.responder = responder;
    this.response = response;
    this.reportState = ReportState.ANSWERED;
    this.respondAt = LocalDateTime.now();
  }

  public void updateReportState(ReportState reportState) {
    this.reportState = reportState;
  }
}
