package com.aloc.aloc.scraper;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SolvedCheckingService {
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final String BASE_URL =
      "https://www.acmicpc.net/status?problem_id=%d&user_id=%s&language_id=-1&result_id=4";

  public boolean isProblemSolved(
      String baekjoonId, Problem problem, UserCourseProblem userCourseProblem) {
    LocalDate recentlySolvedDate = getRecentlySolvedDate(baekjoonId, problem.getProblemId());
    if (recentlySolvedDate == null) {
      return false; // 최근에 푼 날짜가 없으면 문제를 풀지 않은 것으로 간주
    }
    return isSolvedBefore(recentlySolvedDate, LocalDate.from(userCourseProblem.getUpdatedAt()));
  }

  private boolean isSolvedBefore(LocalDate solvedDate, LocalDate openedDate) {
    return solvedDate.isAfter(openedDate) || solvedDate.isEqual(openedDate);
  }

  private LocalDate getRecentlySolvedDate(String baekjoonId, Integer problemId) {
    return parseRecentlySolvedDate(fetchPageContent(buildBaekjoonUrl(baekjoonId, problemId)));
  }

  private String buildBaekjoonUrl(String baekjoonId, Integer problemId) {
    return String.format(BASE_URL, problemId, baekjoonId);
  }

  private String fetchPageContent(String url) {
    try {
      return Jsoup.connect(url).get().html();
    } catch (IOException e) {
      throw new RuntimeException("백준에서 문제 풀이 현황을 가져오는 중 오류가 발생했습니다.");
    }
  }

  private LocalDate parseRecentlySolvedDate(String pageContent) {
    Document doc = Jsoup.parse(pageContent);
    Element tbody = doc.select("tbody").first();

    // 채점 현황에서 내 제출을 불러온 후, 제출한 문제 중 가장 최근 것이 오늘인지 확인합니다.
    if (tbody != null && tbody.select("tr").first() != null) {
      Elements tdList = tbody.select("tr").first().select("td");
      String recentlySolvedDateStr = tdList.last().select("a").attr("title");
      String datePart = recentlySolvedDateStr.substring(0, 10); // "2024-07-20" 추출
      return LocalDate.parse(datePart, FORMATTER);
    }
    return null;
  }
}
