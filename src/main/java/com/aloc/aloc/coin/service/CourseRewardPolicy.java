package com.aloc.aloc.coin.service;

import com.aloc.aloc.coin.dto.response.CoinResponseDto;
import com.aloc.aloc.coin.enums.CoinType;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CourseRewardPolicy implements CoinRewardPolicy {
  private static final int COURSE_REWARD = 20;

  @Override
  public boolean supports(User user, UserCourseProblem userCourseProblem) {
    Course course = userCourseProblem.getUserCourse().getCourse();
    List<UserCourseProblem> sorted =
        userCourseProblem.getUserCourse().getUserCourseProblemList().stream()
            .sorted(Comparator.comparing(UserCourseProblem::getCreatedAt))
            .toList();
    int idx = sorted.indexOf(userCourseProblem);
    return idx == course.getProblemCnt() - 1; // 마지막 문제일 경우
  }

  @Override
  public CoinResponseDto apply(User user, UserCourseProblem userCourseProblem) {
    Course course = userCourseProblem.getUserCourse().getCourse();
    int added = course.getProblemCnt() * COURSE_REWARD;
    return CoinResponseDto.of(
        user.getCoin(), added, CoinType.COURSE_REWARD, course.getTitle() + "코스 해결 보상");
  }
}
