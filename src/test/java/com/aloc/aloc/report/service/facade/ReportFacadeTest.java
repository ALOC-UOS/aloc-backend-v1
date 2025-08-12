package com.aloc.aloc.report.service.facade;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportFacadeTest {

  @Mock private UserService userService;

  @InjectMocks private ReportFacade reportFacade;

  @Test
  @DisplayName("사용자명으로 사용자 조회 성공")
  void findUserByUsernameSuccess() {
    // given
    String username = "test_user";
    User expectedUser = TestFixture.getMockUserByOauthId(username);

    given(userService.getUser(username)).willReturn(expectedUser);

    // when
    User result = reportFacade.findUserByUsername(username);

    // then
    assertThat(result).isEqualTo(expectedUser);
    assertThat(result.getOauthId()).isEqualTo(username);
    then(userService).should().getUser(username);
  }

  @Test
  @DisplayName("존재하지 않는 사용자명으로 조회 실패")
  void findUserByUsernameNotFound() {
    // given
    String invalidUsername = "invalid_user";

    given(userService.getUser(invalidUsername))
        .willThrow(new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

    // when & then
    assertThatThrownBy(() -> reportFacade.findUserByUsername(invalidUsername))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("해당 사용자가 존재하지 않습니다.");

    then(userService).should().getUser(invalidUsername);
  }
}
