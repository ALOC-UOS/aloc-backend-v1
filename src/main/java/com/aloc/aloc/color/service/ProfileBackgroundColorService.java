package com.aloc.aloc.color.service;

import com.aloc.aloc.color.ProfileBackgroundColor;
import com.aloc.aloc.color.dto.response.ProfileBackgroundColorResponseDto;
import com.aloc.aloc.color.repository.ProfileBackgroundColorRepository;
import com.aloc.aloc.user.entity.User;
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

  private String pickColor() {
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

  public ProfileBackgroundColorResponseDto changeColor(String oauthId) {
    User user = userRepository.findByOauthId(oauthId).orElseThrow();
    if (user.getCoin() < COLOR_CHANGE_MONEY) {
      throw new IllegalArgumentException("코인이 부족합니다.");
    }
    user.setCoin(user.getCoin() - COLOR_CHANGE_MONEY);

    String colorName = pickColor();
    ProfileBackgroundColor profileBackgroundColor =
        profileBackgroundColorRepository.findById(colorName).orElseThrow();
    user.setProfileColor(colorName);

    userRepository.save(user);
    return ProfileBackgroundColorResponseDto.of(user.getCoin(), profileBackgroundColor);
  }
}
