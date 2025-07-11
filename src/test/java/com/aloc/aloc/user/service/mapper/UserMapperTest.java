package com.aloc.aloc.user.service.mapper;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
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
  @DisplayName("UserDetailResponseDto 매핑 테스트")
  class MapToUserDetailResponseDtoTest {

    @Test
    @DisplayName("오늘 문제를 푼 유저의 DTO 매핑 성공")
    void mapToUserDetailResponseDto_TodaySolved() {
      // given
      User user = TestFixture.getMockUserByOauthId("testUser");
      user.setLastSolvedAt(LocalDateTime.now()); // 오늘 풀었음
      
      ProfileBackgroundColor mockColor = createMockProfileBackgroundColor();
      given(profileBackgroundColorService.getColorByName(user.getProfileColor()))
          .willReturn(mockColor);

      // when
      UserDetailResponseDto result = userMapper.mapToUserDetailResponseDto(user);

      // then
      assertThat(result).isNotNull();
      assertThat(result.getName()).isEqualTo(user.getName());
      assertThat(result.getBaekjoonId()).isEqualTo(user.getBaekjoonId());
      assertThat(result.getRank()).isEqualTo(user.getRank());
      assertThat(result.getCoin()).isEqualTo(user.getCoin());
      assertThat(result.getSolvedCount()).isEqualTo(user.getSolvedCount());
      assertThat(result.getConsecutiveSolvedDays()).isEqualTo(user.getConsecutiveSolvedDays());
      assertThat(result.isTodaySolved()).isTrue(); // 오늘 풀었으므로 true
    }

    @Test
    @DisplayName("오늘 문제를 풀지 않은 유저의 DTO 매핑 성공")
    void mapToUserDetailResponseDto_NotTodaySolved() {
      // given
      User user = TestFixture.getMockUserByOauthId("testUser");
      user.setLastSolvedAt(LocalDateTime.now().minusDays(1)); // 어제 풀었음
      
      ProfileBackgroundColor mockColor = createMockProfileBackgroundColor();
      given(profileBackgroundColorService.getColorByName(user.getProfileColor()))
          .willReturn(mockColor);

      // when
      UserDetailResponseDto result = userMapper.mapToUserDetailResponseDto(user);

      // then
      assertThat(result).isNotNull();
      assertThat(result.isTodaySolved()).isFalse(); // 어제 풀었으므로 false
    }

    @Test
    @DisplayName("한 번도 문제를 풀지 않은 유저의 DTO 매핑 성공")
    void mapToUserDetailResponseDto_NeverSolved() {
      // given
      User user = TestFixture.getMockUserByOauthId("testUser");
      user.setLastSolvedAt(null); // 한 번도 풀지 않음
      
      ProfileBackgroundColor mockColor = createMockProfileBackgroundColor();
      given(profileBackgroundColorService.getColorByName(user.getProfileColor()))
          .willReturn(mockColor);

      // when
      UserDetailResponseDto result = userMapper.mapToUserDetailResponseDto(user);

      // then
      assertThat(result).isNotNull();
      assertThat(result.isTodaySolved()).isFalse(); // null이므로 false
    }

    @Test
    @DisplayName("어제 자정 직전에 푼 문제의 DTO 매핑")
    void mapToUserDetailResponseDto_LastNightSolved() {
      // given
      User user = TestFixture.getMockUserByOauthId("testUser");
      user.setLastSolvedAt(LocalDateTime.now().minusDays(1).withHour(23).withMinute(59));
      
      ProfileBackgroundColor mockColor = createMockProfileBackgroundColor();
      given(profileBackgroundColorService.getColorByName(user.getProfileColor()))
          .willReturn(mockColor);

      // when
      UserDetailResponseDto result = userMapper.mapToUserDetailResponseDto(user);

      // then
      assertThat(result).isNotNull();
      assertThat(result.isTodaySolved()).isFalse(); // 어제이므로 false
    }

    @Test
    @DisplayName("오늘 자정 직후에 푼 문제의 DTO 매핑")
    void mapToUserDetailResponseDto_TodayMidnightSolved() {
      // given
      User user = TestFixture.getMockUserByOauthId("testUser");
      user.setLastSolvedAt(LocalDateTime.now().withHour(0).withMinute(1));
      
      ProfileBackgroundColor mockColor = createMockProfileBackgroundColor();
      given(profileBackgroundColorService.getColorByName(user.getProfileColor()))
          .willReturn(mockColor);

      // when
      UserDetailResponseDto result = userMapper.mapToUserDetailResponseDto(user);

      // then
      assertThat(result).isNotNull();
      assertThat(result.isTodaySolved()).isTrue(); // 오늘이므로 true
    }

    @Test
    @DisplayName("모든 유저 필드가 정확히 매핑되는지 확인")
    void mapToUserDetailResponseDto_AllFieldsMapping() {
      // given
      User user = TestFixture.getMockUserByOauthId("testUser");
      user.setName("테스트유저");
      user.setBaekjoonId("testBaekjoon");
      user.setRank(15);
      user.setCoin(250);
      user.setSolvedCount(42);
      user.setConsecutiveSolvedDays(7);
      
      ProfileBackgroundColor mockColor = createMockProfileBackgroundColor();
      given(profileBackgroundColorService.getColorByName(user.getProfileColor()))
          .willReturn(mockColor);

      // when
      UserDetailResponseDto result = userMapper.mapToUserDetailResponseDto(user);

      // then
      assertThat(result.getName()).isEqualTo("테스트유저");
      assertThat(result.getBaekjoonId()).isEqualTo("testBaekjoon");
      assertThat(result.getRank()).isEqualTo(15);
      assertThat(result.getCoin()).isEqualTo(250);
      assertThat(result.getSolvedCount()).isEqualTo(42);
      assertThat(result.getConsecutiveSolvedDays()).isEqualTo(7);
      assertThat(result.getAuthority()).isEqualTo(user.getAuthority());
      assertThat(result.getColor()).isNotNull();
    }
  }

  private ProfileBackgroundColor createMockProfileBackgroundColor() {
    return new ProfileBackgroundColor(
        "Blue", "#0066CC", "#0080FF", "#3399FF", "#66B3FF", "#99CCFF", "gradient", 45);
  }
}