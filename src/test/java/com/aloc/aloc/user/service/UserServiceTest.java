package com.aloc.aloc.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  @Test
  @DisplayName("유효한 oauthId로 유저 조회 성공")
  void getUserSuccess() {
    // given
    String oauthId = "oauthId";
    User user = TestFixture.getMockUserByOauthId(oauthId);
    given(userRepository.findByOauthId(oauthId)).willReturn(Optional.of(user));

    // when
    User result = userService.getUser(oauthId);

    // then
    assertThat(result).isEqualTo(user);
  }

  @Test
  @DisplayName("존재하지 않는 oauthId 조회 시 IllegalArgumentException 발생")
  void getUserFail() {
    // given
    String invalidOauthId = "invalid_id";
    given(userRepository.findByOauthId(invalidOauthId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.getUser(invalidOauthId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("해당 사용자가 존재하지 않습니다.");
  }
}
