package com.aloc.aloc.user.dto.response;

import static org.assertj.core.api.Assertions.*;

import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ColorResponseDtoTest {

  @Test
  @DisplayName("ProfileBackgroundColor로부터 ColorResponseDto 생성 성공")
  void of() {
    // given
    ProfileBackgroundColor color = new ProfileBackgroundColor(
        "Blue", "#0066CC", "#0080FF", "#3399FF", "#66B3FF", "#99CCFF", "gradient", 45);

    // when
    ColorResponseDto result = ColorResponseDto.of(color);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Blue");
    assertThat(result.getColor1()).isEqualTo("#0066CC");
    assertThat(result.getColor2()).isEqualTo("#0080FF");
    assertThat(result.getColor3()).isEqualTo("#3399FF");
    assertThat(result.getColor4()).isEqualTo("#66B3FF");
    assertThat(result.getColor5()).isEqualTo("#99CCFF");
    assertThat(result.getType()).isEqualTo("gradient");
    assertThat(result.getDegree()).isEqualTo(45);
  }

  @Test
  @DisplayName("null 값이 포함된 ProfileBackgroundColor로부터 DTO 생성")
  void of_WithNullValues() {
    // given
    ProfileBackgroundColor color = new ProfileBackgroundColor(
        "Red", "#FF0000", null, null, null, null, "solid", null);

    // when
    ColorResponseDto result = ColorResponseDto.of(color);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Red");
    assertThat(result.getColor1()).isEqualTo("#FF0000");
    assertThat(result.getColor2()).isNull();
    assertThat(result.getColor3()).isNull();
    assertThat(result.getColor4()).isNull();
    assertThat(result.getColor5()).isNull();
    assertThat(result.getType()).isEqualTo("solid");
    assertThat(result.getDegree()).isNull();
  }
}
