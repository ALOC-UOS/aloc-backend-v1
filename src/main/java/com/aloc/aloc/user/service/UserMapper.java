package com.aloc.aloc.user.service;

import com.aloc.aloc.color.ProfileBackgroundColor;
import com.aloc.aloc.color.service.ProfileBackgroundColorService;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
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
        user,
        userProfileBackgroundColor.getType(),
        userProfileBackgroundColor.getColor1(),
        userProfileBackgroundColor.getColor2(),
        userProfileBackgroundColor.getColor3(),
        userProfileBackgroundColor.getColor4(),
        userProfileBackgroundColor.getColor5(),
        userProfileBackgroundColor.getDegree());
  }
}
