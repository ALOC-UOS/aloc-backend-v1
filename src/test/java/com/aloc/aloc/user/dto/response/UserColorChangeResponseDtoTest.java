package com.aloc.aloc.user.dto.response;

import static org.assertj.core.api.Assertions.*;

import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserColorChangeResponseDtoTest {

  @Test
  @DisplayName("코인과 ProfileBackgroundColor로 UserColorChangeResponseDto 생성 성공")
  void of() {
    // given
    int userCoin = 75;
    ProfileBackgroundColor color = new ProfileBackgroundColor(
        "Purple", "#8A2BE2", "#9370DB", "#BA55D3", "#DA70D6", "#DDA0DD", "gradient", 90);

    // when
    UserColorChangeResponseDto result = UserColorChangeResponseDto.of(userCoin, color);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getUserCoin()).isEqualTo(75);
    assertThat(result.getColor()).isNotNull();
    assertThat(result.getColor().getName()).isEqualTo("Purple");
    assertThat(result.getColor().getColor1()).isEqualTo("#8A2BE2");
    assertThat(result.getColor().getType()).isEqualTo("gradient");
    assertThat(result.getColor().getDegree()).isEqualTo(90);
  }

  @Test
  @DisplayName("코인이 0일 때의 DTO 생성")
  void of_WithZeroCoin() {
    // given
    int userCoin = 0;
    ProfileBackgroundColor color = new ProfileBackgroundColor(
        "Black", "#000000", null, null, null, null, "solid", null);

    // when
    UserColorChangeResponseDto result = UserColorChangeResponseDto.of(userCoin, color);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getUserCoin()).isEqualTo(0);
    assertThat(result.getColor()).isNotNull();
    assertThat(result.getColor().getName()).isEqualTo("Black");
    assertThat(result.getColor().getType()).isEqualTo("solid");
  }

  @Test
  @DisplayName("빌더 패턴으로 DTO 생성")
  void builderPattern() {
    // given
    ColorResponseDto colorDto = ColorResponseDto.builder()
        .name("Green")
        .color1("#008000")
        .type("solid")
        .build();

    // when
    UserColorChangeResponseDto result = UserColorChangeResponseDto.builder()
        .userCoin(120)
        .color(colorDto)
        .build();

    // then
    assertThat(result).isNotNull();
    assertThat(result.getUserCoin()).isEqualTo(120);
    assertThat(result.getColor()).isEqualTo(colorDto);
    assertThat(result.getColor().getName()).isEqualTo("Green");
  }
}
