package com.aloc.aloc.webhook;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.problem.entity.Problem;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class DiscordWebhookService {
  private final RestTemplate restTemplate;

  @Value("${discord.webhook.url}")
  private String discordWebhookUrl;

  public void sendNotification(String message) {
    Map<String, String> request = new HashMap<>();
    request.put("content", message);
    restTemplate.postForObject(discordWebhookUrl, request, String.class);
  }

  public void sendScrapResultEmbed(Course course, List<Problem> problems) {
    List<Map<String, Object>> embeds = new ArrayList<>();

    // 1. 코스 정보 Embed
    Map<String, Object> courseEmbed = new HashMap<>();
    courseEmbed.put("title", "📌 크롤링 결과");
    courseEmbed.put("color", 0x1E90FF); // DodgerBlue

    StringBuilder description =
        new StringBuilder()
            .append("📚 **코스**: ")
            .append(course.getTitle())
            .append("\n")
            .append("📝 **설명**: ")
            .append(course.getDescription())
            .append("\n")
            .append("📊 **유형**: ")
            .append(course.getCourseType())
            .append("\n")
            .append("🎯 **목표 문제 수**: ")
            .append(course.getProblemCnt())
            .append("개\n")
            .append("🆕 **크롤링된 문제 수**: ")
            .append(problems.size())
            .append("개\n")
            .append("🔢 **난이도 범위**: ")
            .append(course.getMinRank())
            .append(" ~ ")
            .append(course.getMaxRank())
            .append("\n")
            .append("📈 **평균 난이도**: ")
            .append(course.getAverageRank())
            .append("\n")
            .append("🗓️ **기한**: ")
            .append(course.getDuration());

    courseEmbed.put("description", description.toString());

    // author
    Map<String, Object> author = new HashMap<>();
    author.put("name", "ALOC 크롤러 🤖");
    author.put("icon_url", "https://cdn-icons-png.flaticon.com/512/4712/4712107.png"); // 예시 아이콘
    courseEmbed.put("author", author);

    // footer
    Map<String, Object> footer = new HashMap<>();
    footer.put("text", "알고리즘 문제 자동 크롤링 결과");
    courseEmbed.put("footer", footer);

    // timestamp
    courseEmbed.put("timestamp", Instant.now().toString());

    embeds.add(courseEmbed);

    // 2. 문제 정보 Embed (25개씩 분할)
    int chunkSize = 25;
    for (int i = 0; i < problems.size(); i += chunkSize) {
      List<Problem> chunk = problems.subList(i, Math.min(i + chunkSize, problems.size()));
      List<Map<String, Object>> fields = new ArrayList<>();

      for (Problem p : chunk) {
        Map<String, Object> field = new HashMap<>();
        String problemUrl = "https://www.acmicpc.net/problem/" + p.getProblemId();

        field.put(
            "name", "🔹 [" + p.getProblemId() + " - " + p.getTitle() + "](" + problemUrl + ")");
        field.put("value", "⭐️ 난이도: " + p.getRank() + "\n🏷 알고리즘: " + getKoreanAlgorithmNames(p));
        field.put("inline", false);
        fields.add(field);
      }

      Map<String, Object> problemEmbed = new HashMap<>();
      problemEmbed.put("title", "📘 크롤링된 문제 목록");
      problemEmbed.put("fields", fields);
      problemEmbed.put("color", 0x32CD32); // LimeGreen

      // Optional: footer/timestamp 넣을 수도 있음
      problemEmbed.put("timestamp", Instant.now().toString());

      embeds.add(problemEmbed);
    }

    // 3. 전송
    Map<String, Object> request = new HashMap<>();
    request.put("content", "크롤링이 완료되었습니다! 🎉");
    request.put("embeds", embeds);
    request.put("allowed_mentions", Map.of("parse", List.of("everyone"))); // 멘션 허용

    restTemplate.postForObject(discordWebhookUrl, request, String.class);
  }

  private String getKoreanAlgorithmNames(Problem problem) {
    if (problem.getProblemAlgorithmList() == null || problem.getProblemAlgorithmList().isEmpty()) {
      return "없음";
    }
    return problem.getProblemAlgorithmList().stream()
        .map(pa -> pa.getAlgorithm().getKoreanName())
        .collect(Collectors.joining(", "));
  }
}
