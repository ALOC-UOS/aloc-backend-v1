package com.aloc.aloc.course.service;

import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.course.repository.UserCourseRepository;
import com.aloc.aloc.user.entity.User;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserCourseService {
  private final UserCourseRepository userCourseRepository;

  public List<UserCourse> getUserCoursesByUser(User user) {
    return userCourseRepository.findAllByUser(user);
  }
}
