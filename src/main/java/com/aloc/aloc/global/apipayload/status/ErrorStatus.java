package com.aloc.aloc.global.apipayload.status;

import com.aloc.aloc.global.apipayload.code.BaseErrorCode;
import com.aloc.aloc.global.apipayload.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
  // 가장 일반적인 응답
  _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
  _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
  _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
  _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
  _LOGIN_FAILURE(HttpStatus.BAD_REQUEST, "COMMON400", "Login Fail"),
  _CONFLICT(HttpStatus.CONFLICT, "COMMON409", "이미 시도한 요청입니다."),
  _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "데이터가 존재하지 않습니다."),
  _UNPROCESSABLE_ENTITY(
      HttpStatus.UNPROCESSABLE_ENTITY, "COMMON422", "요청은 문법적으로 맞지만 의미상으로 처리할 수 없다");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public ErrorReasonDto getReason() {
    return ErrorReasonDto.builder().message(message).code(code).isSuccess(false).build();
  }

  @Override
  public ErrorReasonDto getReasonHttpStatus() {
    return ErrorReasonDto.builder()
        .message(message)
        .code(code)
        .isSuccess(false)
        .httpStatus(httpStatus)
        .build();
  }
}
