package com.aloc.aloc.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 문의사항 답변 요청")
public class AdminReportRequestDto {

  @NotBlank(message = "답변 내용은 필수입니다.")
  @Schema(description = "답변 내용", example = "해당 문제는 서버 점검 중에 발생한 일시적 오류입니다. 현재 해결되었습니다.")
  private String response;
}
