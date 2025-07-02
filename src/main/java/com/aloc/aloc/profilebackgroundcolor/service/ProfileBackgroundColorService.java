package com.aloc.aloc.profilebackgroundcolor.service;

import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import com.aloc.aloc.profilebackgroundcolor.dto.response.ProfileColorListResponseDto;
import com.aloc.aloc.profilebackgroundcolor.repository.ProfileBackgroundColorRepository;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileBackgroundColorService {

  private final ProfileBackgroundColorRepository profileBackgroundColorRepository;
  private static final Random RANDOM = new Random();

  public ProfileBackgroundColor getColorByName(String name) {
    return profileBackgroundColorRepository
        .findByName(name)
        .orElseThrow(() -> new IllegalArgumentException("해당 컬러가 없습니다: " + name));
  }

  public ProfileBackgroundColor pickRandomColor() {
    int draw = RANDOM.nextInt(100) + 1;

    List<ProfileBackgroundColor> colors;
    if (draw <= 85) {
      colors = profileBackgroundColorRepository.findByType("common");
    } else if (draw <= 95) {
      colors = profileBackgroundColorRepository.findByType("rare");
    } else {
      colors = profileBackgroundColorRepository.findByType("special");
    }

    return colors.get(RANDOM.nextInt(colors.size()));
  }

  public List<ProfileColorListResponseDto> getAllColors() {
    return profileBackgroundColorRepository.findAll().stream()
        .map(ProfileColorListResponseDto::fromEntity)
        .toList();
  }
}
