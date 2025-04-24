package com.aloc.aloc.global.login.handler;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.global.apipayload.status.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    CustomApiResponse<String> apiResponse =
        CustomApiResponse.onFailure(
            ErrorStatus._LOGIN_FAILURE, "Username or password is incorrect");
    objectMapper.writeValue(response.getWriter(), apiResponse);
    log.error(Exception.class.getName() + " : " + exception.getMessage());
    log.info("로그인 실패");
  }
}
