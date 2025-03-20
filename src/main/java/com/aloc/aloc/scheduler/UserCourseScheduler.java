package com.aloc.aloc.scheduler;

import com.aloc.aloc.course.service.UserCourseService;
import com.aloc.aloc.user.service.UserService;
import com.aloc.aloc.webhook.DiscordWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCourseScheduler {
  private final UserCourseService userCourseService;
  private final UserService userService;
  private final DiscordWebhookService discordWebhookService;

  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void updateDailyUserCourseProblems() {
    log.info("🔄 UserCourseProblem 상태 업데이트 시작...");

    try {
      userCourseService.closeFailUserCourse();
      userCourseService.openDailyUserCourseProblem();
      discordWebhookService.sendNotification("✅ 실패한 코스 처리 및 daily 코스 문제 공개");
      userService.initializeUserStreakDays();
      discordWebhookService.sendNotification("✅ 유저 연속 문제 풀이 기록 초기화 완료!");
    } catch (Exception e) {
      log.error("❌ UserCourse관련 처리 중 오류 발생: {}", e.getMessage(), e);
    }
  }
}
