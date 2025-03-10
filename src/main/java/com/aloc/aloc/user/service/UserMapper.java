package com.aloc.aloc.user.service;

import com.aloc.aloc.color.Color;
import com.aloc.aloc.color.service.ColorService;
import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
  private final ProblemFacade problemFacade;
  private final ColorService colorService;

  UserDetailResponseDto mapToUserDetailResponseDto(User user) {
    Integer problemCounts = user.getSolvedCount();
    Color userColor = colorService.getColorById(user.getProfileColor());

    return UserDetailResponseDto.of(
        user,
        userColor.getCategory(),
        userColor.getColor1(),
        userColor.getColor2(),
        userColor.getColor3(),
        userColor.getColor4(),
        userColor.getColor5(),
        userColor.getDegree());
  }
}
