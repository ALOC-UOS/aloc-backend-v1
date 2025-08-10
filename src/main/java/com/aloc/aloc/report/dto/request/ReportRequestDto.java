package com.aloc.aloc.report.dto.request;

import com.aloc.aloc.report.enums.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "문의사항 생성 요청")
public class ReportRequestDto {

  @NotNull(message = "문의 유형은 필수입니다.")
  @Schema(description = "문의 유형", example = "BUG")
  private ReportType reportType;

  @NotBlank(message = "제목은 필수입니다.")
  @Schema(description = "문의 제목", example = "로그인 시 오류가 발생합니다")
  private String title;

  @NotBlank(message = "내용은 필수입니다.")
  @Schema(description = "문의 내용", example = "구글 로그인을 시도할 때 500 에러가 발생합니다.")
  private String content;

  @Schema(description = "공개 여부", example = "false")
  private Boolean isPublic;
}
