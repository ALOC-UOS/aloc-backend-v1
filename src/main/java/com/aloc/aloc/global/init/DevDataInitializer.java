package com.aloc.aloc.global.init;

import com.aloc.aloc.profilebackgroundcolor.entity.ProfileBackgroundColor;
import com.aloc.aloc.profilebackgroundcolor.repository.ProfileBackgroundColorRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevDataInitializer implements CommandLineRunner {
  private final ProfileBackgroundColorRepository profileBackgroundColorRepository;

  @Override
  public void run(String... args) throws Exception {
    initProfileBackgroundColor();
  }

  private void initProfileBackgroundColor() {
    if (profileBackgroundColorRepository.count() > 0) {
      log.info("🌈 profile_background_color 초기 데이터가 이미 존재합니다.");
      return;
    }

    List<ProfileBackgroundColor> colors =
        List.of(
            new ProfileBackgroundColor("Red", "#FF5A5A", null, null, null, null, "common", null),
            new ProfileBackgroundColor("Yellow", "#FFB800", null, null, null, null, "common", null),
            new ProfileBackgroundColor("Orange", "#FF9635", null, null, null, null, "common", null),
            new ProfileBackgroundColor("Green", "#2ADC0D", null, null, null, null, "common", null),
            new ProfileBackgroundColor(
                "Emerald", "#00DC9A", null, null, null, null, "common", null),
            new ProfileBackgroundColor(
                "Skyblue", "#00C2FF", null, null, null, null, "common", null),
            new ProfileBackgroundColor("Blue", "#408CFF", null, null, null, null, "common", null),
            new ProfileBackgroundColor("Indigo", "#4440FF", null, null, null, null, "common", null),
            new ProfileBackgroundColor("Purple", "#BA63FF", null, null, null, null, "common", null),
            new ProfileBackgroundColor("Pink", "#FF5AB3", null, null, null, null, "common", null),
            new ProfileBackgroundColor(
                "PurpleRed", "#BA63FF", "#FF5A5A", null, null, null, "rare", 135),
            new ProfileBackgroundColor(
                "BluePurple", "#408CFF", "#BA63FF", null, null, null, "rare", 135),
            new ProfileBackgroundColor(
                "IndigoPink", "#4440FF", "#FF5AB3", null, null, null, "rare", 135),
            new ProfileBackgroundColor(
                "SkyblueIndigo", "#00C2FF", "#4440FF", null, null, null, "rare", 135),
            new ProfileBackgroundColor(
                "GreenSkyblue", "#2ADC0D", "#00C2FF", null, null, null, "rare", 135),
            new ProfileBackgroundColor(
                "RedYellow", "#FF5A5A", "#FFB800", null, null, null, "rare", 135),
            new ProfileBackgroundColor(
                "BeautifulYPB", "#FFB800", "#FF69F0", "#408CFF", "", "", "special", 135),
            new ProfileBackgroundColor(
                "BeautifulBPR", "#408CFF", "#E95FFF", "#FF5A5A", "", "", "special", 135),
            new ProfileBackgroundColor(
                "GreenTea", "#CED690", "#49985D", null, null, null, "rare", 180),
            new ProfileBackgroundColor(
                "TequilaSunrise", "#F2F1AF", "#EF3529", null, null, null, "rare", 180),
            new ProfileBackgroundColor("10AM", "#759CFF", "#B2E3FF", null, null, null, "rare", 180),
            new ProfileBackgroundColor(
                "10PM", "#111579", "#0071C3", null, null, null, "rare", 180));

    profileBackgroundColorRepository.saveAll(colors);
    log.info("✅ profile_background_color 초기 데이터 삽입 완료: {}개", colors.size());
  }
}
