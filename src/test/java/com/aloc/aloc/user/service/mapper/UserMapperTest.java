package com.aloc.aloc.user.service.mapper;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.profilebackgroundcolor.entity.ProfileBackgroundColor;
import com.aloc.aloc.profilebackgroundcolor.service.ProfileBackgroundColorService;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

  @Mock private ProfileBackgroundColorService profileBackgroundColorService;
  @InjectMocks private UserMapper userMapper;

  @Nested
  @DisplayName("오늘 풀이 여부 판별 테스트")
  class TodaySolvedTest {

    @Test
    @DisplayName("오늘 문제를 푼 유저는 todaySolved가 true")
    void mapToUserDetailResponseDtoTodaySolved() {
      // given
      User user = TestFixture.getMockUserByOauthId("testUser");
      user.setLastSolvedAt(LocalDateTime.now()); // 오늘 풀었음

      ProfileBackgroundColor mockColor = createMockProfileBackgroundColor();
      given(profileBackgroundColorService.getColorByName(user.getProfileColor()))
          .willReturn(mockColor);

      // when
      UserDetailResponseDto result = userMapper.mapToUserDetailResponseDto(user);

      // then
      assertThat(result.isTodaySolved()).isTrue();
    }

    @Test
    @DisplayName("어제 문제를 푼 유저는 todaySolved가 false")
    void mapToUserDetailResponseDtoNotTodaySolved() {
      // given
      User user = TestFixture.getMockUserByOauthId("testUser");
      user.setLastSolvedAt(LocalDateTime.now().minusDays(1)); // 어제 풀었음

      ProfileBackgroundColor mockColor = createMockProfileBackgroundColor();
      given(profileBackgroundColorService.getColorByName(user.getProfileColor()))
          .willReturn(mockColor);

      // when
      UserDetailResponseDto result = userMapper.mapToUserDetailResponseDto(user);

      // then
      assertThat(result.isTodaySolved()).isFalse();
    }

    @Test
    @DisplayName("한 번도 문제를 풀지 않은 유저는 todaySolved가 false")
    void mapToUserDetailResponseDtoNeverSolved() {
      // given
      User user = TestFixture.getMockUserByOauthId("testUser");
      user.setLastSolvedAt(null); // 한 번도 풀지 않음

      ProfileBackgroundColor mockColor = createMockProfileBackgroundColor();
      given(profileBackgroundColorService.getColorByName(user.getProfileColor()))
          .willReturn(mockColor);

      // when
      UserDetailResponseDto result = userMapper.mapToUserDetailResponseDto(user);

      // then
      assertThat(result.isTodaySolved()).isFalse();
    }

    @Test
    @DisplayName("오늘 자정 직후에 푼 문제는 todaySolved가 true")
    void mapToUserDetailResponseDtoTodayMidnightSolved() {
      // given
      User user = TestFixture.getMockUserByOauthId("testUser");
      user.setLastSolvedAt(LocalDateTime.now().withHour(0).withMinute(1)); // 오늘 00:01

      ProfileBackgroundColor mockColor = createMockProfileBackgroundColor();
      given(profileBackgroundColorService.getColorByName(user.getProfileColor()))
          .willReturn(mockColor);

      // when
      UserDetailResponseDto result = userMapper.mapToUserDetailResponseDto(user);

      // then
      assertThat(result.isTodaySolved()).isTrue();
    }

    @Test
    @DisplayName("어제 23:59에 푼 문제는 todaySolved가 false")
    void mapToUserDetailResponseDtoLastNightSolved() {
      // given
      User user = TestFixture.getMockUserByOauthId("testUser");
      user.setLastSolvedAt(
          LocalDateTime.now().minusDays(1).withHour(23).withMinute(59)); // 어제 23:59

      ProfileBackgroundColor mockColor = createMockProfileBackgroundColor();
      given(profileBackgroundColorService.getColorByName(user.getProfileColor()))
          .willReturn(mockColor);

      // when
      UserDetailResponseDto result = userMapper.mapToUserDetailResponseDto(user);

      // then
      assertThat(result.isTodaySolved()).isFalse();
    }
  }

  private ProfileBackgroundColor createMockProfileBackgroundColor() {
    return new ProfileBackgroundColor(
        "Blue", "#0066CC", "#0080FF", "#3399FF", "#66B3FF", "#99CCFF", "gradient", 45);
  }
}
