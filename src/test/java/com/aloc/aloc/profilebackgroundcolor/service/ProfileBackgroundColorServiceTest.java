package com.aloc.aloc.profilebackgroundcolor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.profilebackgroundcolor.entity.ProfileBackgroundColor;
import com.aloc.aloc.profilebackgroundcolor.repository.ProfileBackgroundColorRepository;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// 테스트 함수 목록
// [getColorByName] 정상 케이스 - 존재하는 색상 이름으로 조회
// [getColorByName] 예외 케이스 - 존재하지 않는 색상 이름으로 조회
// [pickColor] common 타입 색상 선택 테스트

@ExtendWith(MockitoExtension.class)
public class ProfileBackgroundColorServiceTest{

    @Mock
    private ProfileBackgroundColorRepository profileBackgroundColorRepository;

    @InjectMocks
    private ProfileBackgroundColorService profileBackgroundColorService;

    // [getColorByName] 정상 케이스 - 존재하는 색상 이름으로 조회
    @Test
    void getColorByName_normalCase() {
        // given
        String colorName = "TestColor";
        ProfileBackgroundColor expectedColor = TestFixture.getMockProfileBackgroundColor();
        given(profileBackgroundColorRepository.findByName(colorName)).willReturn(Optional.of(expectedColor));

        // when
        ProfileBackgroundColor result = profileBackgroundColorService.getColorByName(colorName);

        // then
        assertThat(result).isEqualTo(expectedColor);
        assertThat(result.getName()).isEqualTo(colorName);
        assertThat(result.getType()).isEqualTo(expectedColor.getType());
        assertThat(result.getColor1()).isEqualTo(expectedColor.getColor1());
        assertThat(result.getColor2()).isEqualTo(expectedColor.getColor2());
        assertThat(result.getColor3()).isEqualTo(expectedColor.getColor3());
        assertThat(result.getColor4()).isEqualTo(expectedColor.getColor4());
        assertThat(result.getColor5()).isEqualTo(expectedColor.getColor5());
        assertThat(result.getDegree()).isEqualTo(expectedColor.getDegree());
        verify(profileBackgroundColorRepository).findByName(colorName);
    }

    // [getColorByName] 예외 케이스 - 존재하지 않는 색상 이름으로 조회
    @Test
    void getColorByName_exceptionCase() {
        // given
        String nonExistentColorName = "NonExistentColor";
        given(profileBackgroundColorRepository.findByName(nonExistentColorName)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> profileBackgroundColorService.getColorByName(nonExistentColorName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("해당 컬러가 없습니다. " + nonExistentColorName);

        // then
        verify(profileBackgroundColorRepository).findByName(nonExistentColorName);
    }

    // [pickColor] common 타입 색상 선택 테스트
    @Test
    void pickColor_commonType() {
        // given
        List<ProfileBackgroundColor> commonColors = List.of(
            TestFixture.getMockProfileBackgroundColorByType("common"),
            TestFixture.getMockProfileBackgroundColorByName("Blue", "common")
        );
        given(profileBackgroundColorRepository.findByType("common")).willReturn(commonColors);

        // when
        String result = profileBackgroundColorService.pickColor();

        // then
        assertThat(result).isIn("Blue");
        verify(profileBackgroundColorRepository).findByType("common");
    }
}