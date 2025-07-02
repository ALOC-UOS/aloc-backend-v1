package com.aloc.aloc.profilebackgroundcolor.service;

import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import com.aloc.aloc.profilebackgroundcolor.repository.ProfileBackgroundColorRepository;
import com.aloc.aloc.user.dto.response.ColorResponseDto;
import com.aloc.aloc.user.repository.UserRepository;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileBackgroundColorService {
  private final ProfileBackgroundColorRepository profileBackgroundColorRepository;
  private final UserRepository userRepository;

  private static final int COLOR_CHANGE_MONEY = 100;

  public ProfileBackgroundColor getColorByName(String name) {
    return profileBackgroundColorRepository
        .findByName(name)
        .orElseThrow(() -> new IllegalArgumentException("해당 컬러가 없습니다. " + name));
  }

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

  public List<ColorResponseDto> getAllColors() {
    return profileBackgroundColorRepository.findAll().stream().map(ColorResponseDto::of).toList();
  }
}
