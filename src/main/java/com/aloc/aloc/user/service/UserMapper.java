package com.aloc.aloc.user.service;

import com.aloc.aloc.profilebackgroundcolor.ProfileBackgroundColor;
import com.aloc.aloc.profilebackgroundcolor.service.ProfileBackgroundColorService;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
  private final ProfileBackgroundColorService profileBackgroundColorService;

  UserDetailResponseDto mapToUserDetailResponseDto(User user) {
    ProfileBackgroundColor userProfileBackgroundColor =
        profileBackgroundColorService.getColorByName(user.getProfileColor());

    return UserDetailResponseDto.of(
        user, userProfileBackgroundColor, isTodaySolved(user.getLastSolvedAt()));
  }

  private boolean isTodaySolved(LocalDateTime lastSolvedAt) {
    if (lastSolvedAt == null) {
      return false;
    }
    // 현재 날짜 가져오기
    LocalDate today = LocalDate.now();
    LocalDate lastSolvedDate = lastSolvedAt.toLocalDate();

    return lastSolvedDate.isEqual(today);
  }
}
