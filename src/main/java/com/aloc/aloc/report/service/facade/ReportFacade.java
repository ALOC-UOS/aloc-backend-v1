package com.aloc.aloc.report.service.facade;

import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportFacade {

  private final UserService userService;

  public User findUserByUsername(String username) {
    return userService.getUser(username);
  }
}
