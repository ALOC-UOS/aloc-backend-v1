package com.aloc.aloc.profilebackgroundcolor.controller;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.profilebackgroundcolor.dto.response.ProfileColorListResponseDto;
import com.aloc.aloc.profilebackgroundcolor.service.ProfileBackgroundColorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class BgColorController {
  // 서비스를 생성자로 넣어주고, 나중에 컨트롤러에서 사용함.
  private final ProfileBackgroundColorService profileBackgroundColorService;

  @GetMapping("/colors")
  // 나중에 swagger 추가하기

  public CustomApiResponse<List<ProfileColorListResponseDto>> getAllColors() {
    return CustomApiResponse.onSuccess(profileBackgroundColorService.getAllColors());
  }
}
