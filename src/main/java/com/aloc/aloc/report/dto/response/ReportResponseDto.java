// ReportResponseDto.java
package com.aloc.aloc.report.dto.response;

import com.aloc.aloc.report.entity.Report;
import com.aloc.aloc.report.enums.ReportState;
import com.aloc.aloc.report.enums.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReportResponseDto {

  @Schema(description = "문의사항 ID", example = "1")
  private long id;

  @Schema(description = "문의 유형", example = "BUG")
  private ReportType reportType;

  @Schema(description = "제목", example = "로그인 시 오류가 발생합니다")
  private String title;

  @Schema(description = "내용", example = "구글 로그인을 시도할 때 500 에러가 발생합니다.")
  private String content;

  @Schema(description = "문의자 이름", example = "홍길동")
  private String requesterName;

  @Schema(description = "답변자 이름", example = "관리자")
  private String responderName;

  @Schema(description = "답변 내용")
  private String response;

  @Schema(description = "문의 상태", example = "WAITING")
  private ReportState reportState;

  @Schema(description = "공개 여부", example = "false")
  private Boolean isPublic;

  @Schema(description = "생성일시")
  private LocalDateTime createdAt;

  @Schema(description = "수정일시")
  private LocalDateTime updatedAt;

  @Schema(description = "답변일시")
  private LocalDateTime respondAt;

  public static ReportResponseDto of(Report report) {
    return ReportResponseDto.builder()
        .id(report.getId())
        .reportType(report.getReportType())
        .title(report.getTitle())
        .content(report.getContent())
        .requesterName(report.getRequester().getName())
        .responderName(report.getResponder() != null ? report.getResponder().getName() : null)
        .response(report.getResponse())
        .reportState(report.getReportState())
        .isPublic(report.getIsPublic())
        .createdAt(report.getCreatedAt())
        .updatedAt(report.getUpdatedAt())
        .respondAt(report.getRespondAt())
        .build();
  }
}
