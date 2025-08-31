package com.aloc.aloc.user.service.facade;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.global.apipayload.exception.NoContentException;
import com.aloc.aloc.global.image.ImageService;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import com.aloc.aloc.user.service.UserSortingService;
import com.aloc.aloc.user.service.mapper.UserMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

  @Mock private UserSortingService userSortingService;
  @Mock private UserMapper userMapper;
  @Mock private UserService userService;
  @Mock private ImageService imageService;

  @InjectMocks private UserFacade userFacade;

  @Nested
  @DisplayName("유저 목록 조회 테스트")
  class GetUsersTest {

    @Test
    @DisplayName("활성 유저 목록 조회 성공 - 서비스 간 협력 확인")
    void getUsers() {
      // given
      List<User> mockUsers = List.of(TestFixture.getMockNewUser());
      List<User> sortedUsers = List.of(TestFixture.getMockNewUser());
      UserDetailResponseDto mockDto = createMockUserDetailResponseDto();

      given(userService.getActiveUsers()).willReturn(mockUsers);
      given(userSortingService.sortUserList(mockUsers)).willReturn(sortedUsers);
      given(userMapper.mapToUserDetailResponseDto(any(User.class))).willReturn(mockDto);

      // when
      List<UserDetailResponseDto> result = userFacade.getUsers();

      // then
      assertThat(result).hasSize(1);
      verify(userService).getActiveUsers();
      verify(userSortingService).sortUserList(mockUsers);
      verify(userMapper).mapToUserDetailResponseDto(any(User.class));
    }

    @Test
    @DisplayName("활성 유저가 없을 때 NoContentException 발생")
    void getUsersNoUsers() {
      // given
      given(userService.getActiveUsers()).willReturn(new ArrayList<>());

      // when & then
      assertThatThrownBy(() -> userFacade.getUsers())
          .isInstanceOf(NoContentException.class)
          .hasMessage("조회 가능한 유저가 없습니다.");
    }
  }

  @Nested
  @DisplayName("프로필 이미지 업데이트 테스트")
  class UpdateProfileImageTest {

    @Test
    @DisplayName("기존 이미지가 있을 때 삭제 후 새 이미지 업로드 없이 처리")
    void updateUserProfileImageWithExistingImage() throws Exception {
      // given
      String oauthId = "testOauth";
      User mockUser = TestFixture.getMockUserByOauthId(oauthId);
      UserDetailResponseDto mockResponse = createMockUserDetailResponseDto();

      given(userService.getUser(oauthId)).willReturn(mockUser);
      given(userMapper.mapToUserDetailResponseDto(mockUser)).willReturn(mockResponse);

      // when
      UserDetailResponseDto result = userFacade.updateUserProfileImage(oauthId, null);

      // then - facade가 올바르게 서비스들을 협력시키는지 확인
      assertThat(result).isNotNull();
      verify(userService).getUser(oauthId);
      verify(imageService).deleteImage(anyString(), any(), any()); // 기존 이미지 삭제 호출됨
      verify(userMapper).mapToUserDetailResponseDto(mockUser);
    }
  }

  private UserDetailResponseDto createMockUserDetailResponseDto() {
    return UserDetailResponseDto.builder()
        .name("테스트유저")
        .baekjoonId("testbaekjoon")
        .rank(25)
        .coin(100)
        .solvedCount(50)
        .consecutiveSolvedDays(5)
        .isTodaySolved(true)
        .build();
  }
}
