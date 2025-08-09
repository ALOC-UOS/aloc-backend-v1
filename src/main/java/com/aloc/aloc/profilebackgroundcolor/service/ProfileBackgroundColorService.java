package com.aloc.aloc.profilebackgroundcolor.service;

import com.aloc.aloc.profilebackgroundcolor.entity.ProfileBackgroundColor;
import com.aloc.aloc.profilebackgroundcolor.repository.ProfileBackgroundColorRepository;
import com.aloc.aloc.profilebackgroundcolor.dto.response.ColorResponseDto;
import com.aloc.aloc.user.repository.UserRepository;
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
  private final UserRepository userRepository;

  private static final int COLOR_CHANGE_MONEY = 100;


  // 1. getColorByName 이름으로 조회
  //input : 컬러 이름
  //output : 컬러 이름에 해당하는 컬러
  public ProfileBackgroundColor getColorByName(String name) {
    return profileBackgroundColorRepository
        .findByName(name)
        .orElseThrow(() -> new IllegalArgumentException("해당 컬러가 없습니다. " + name));
  }

  // 2. pickColor 랜덤 컬러 선택
  //input : 없음
  //output : 랜덤 컬러 이름
  public String pickColor() {
    Random random = new Random();
    int draw = random.nextInt(100) + 1;

    List<ProfileBackgroundColor> profileBackgroundColorList;
    if (draw <= 85) {
      profileBackgroundColorList = profileBackgroundColorRepository.findByType("common");
    } else if (draw <= 95) {
      profileBackgroundColorList = profileBackgroundColorRepository.findByType("rare");
    } else {
      profileBackgroundColorList = profileBackgroundColorRepository.findByType("special");
    }

    return profileBackgroundColorList
        .get(random.nextInt(profileBackgroundColorList.size()))
        .getName();
  }

  // 3. getAllColors 모든 컬러 조회
  //input : 없음
  //output : 모든 컬러 리스트
  public List<ColorResponseDto> getAllColors() {
    return profileBackgroundColorRepository.findAll().stream().map(ColorResponseDto::of).toList();
  }
}
