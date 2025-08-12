package com.aloc.aloc.profilebackgroundcolor.service;

import com.aloc.aloc.profilebackgroundcolor.dto.response.ColorResponseDto;
import com.aloc.aloc.profilebackgroundcolor.entity.ProfileBackgroundColor;
import com.aloc.aloc.profilebackgroundcolor.enums.ColorType;
import com.aloc.aloc.profilebackgroundcolor.repository.ProfileBackgroundColorRepository;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


// 프로필 배경 색상 서비스
// 1. getColorByName 이름으로 조회
// 2. pickColor 랜덤 컬러 선택
// 3. getAllColors 모든 컬러 조회
@Service
@RequiredArgsConstructor
public class ProfileBackgroundColorService {
  private final ProfileBackgroundColorRepository profileBackgroundColorRepository;
  private final Random randomNumberGenerator = new Random();

  // 랜덤 컬러 선택 확률 경계 설정
  private static final int MAX_PROBABILITY = 100;
  private static final int COMMON_THRESHOLD = 85;
  private static final int RARE_THRESHOLD = 95;


  // 1. getColorByName 이름으로 조회
  // input : 컬러 이름
  // output : 컬러 이름에 해당하는 컬러
  public ProfileBackgroundColor getColorByName(String colorName) {
    return profileBackgroundColorRepository
        .findByName(colorName)
        .orElseThrow(() -> new IllegalArgumentException("해당 컬러가 없습니다. " + colorName));
  }

  // 2. pickColor 랜덤 컬러 선택
  // input : 없음
  // output : 랜덤 컬러 이름
  public String pickColor() {
    ColorType selectedType = determineColorTypeByProbability();
    List<ProfileBackgroundColor> colorsOfSelectedType = getColorsByType(selectedType);
    return selectRandomColor(colorsOfSelectedType);
  }

  // 확률 기반으로 ColorType 결정
  // input : 없음
  // output : 선택된 ColorType
  private ColorType determineColorTypeByProbability() {
    int randomNumber = randomNumberGenerator.nextInt(MAX_PROBABILITY) + 1;
    
    if (randomNumber <= COMMON_THRESHOLD) {
      return ColorType.COMMON;
    } else if (randomNumber <= RARE_THRESHOLD) {
      return ColorType.RARE;
    } else {
      return ColorType.SPECIAL;
    }
  }

  // 특정 타입의 색상 리스트 조회
  // input : ColorType
  // output : 해당 타입의 색상 리스트
  private List<ProfileBackgroundColor> getColorsByType(ColorType colorType) {
    List<ProfileBackgroundColor> colors = profileBackgroundColorRepository.findByType(colorType.getValue());
    
    if (colors.isEmpty()) {
      throw new IllegalStateException("선택된 타입에 해당하는 색상이 없습니다: " + colorType.getValue());
    }
    
    return colors;
  }

  // 색상 리스트에서 랜덤하게 하나 선택
  // input : 색상 리스트
  // output : 선택된 색상 이름
  private String selectRandomColor(List<ProfileBackgroundColor> colors) {
    return colors.get(randomNumberGenerator.nextInt(colors.size())).getName();
  }

  // 3. getAllColors 모든 컬러 조회
  // input : 없음
  // output : 모든 컬러 리스트
  public List<ColorResponseDto> getAllColors() {
    return profileBackgroundColorRepository.findAll().stream().map(ColorResponseDto::of).toList();
  }
}
