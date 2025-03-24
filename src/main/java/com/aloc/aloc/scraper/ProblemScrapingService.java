package com.aloc.aloc.scraper;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.course.dto.request.CourseRequestDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.entity.CourseProblem;
import com.aloc.aloc.course.repository.CourseProblemRepository;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.ProblemAlgorithm;
import com.aloc.aloc.problem.repository.ProblemAlgorithmRepository;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.webhook.DiscordWebhookService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemScrapingService {

  private static final String HEADER_FIELD_NAME = "User-Agent";
  private static final String HEADER_FIELD_VALUE =
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
          + "(KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3";
  private static final int RUBY_1 = 30;

  private final ProblemService problemService;
  private final AlgorithmService algorithmService;
  private final ProblemAlgorithmRepository problemAlgorithmRepository;
  private final CourseProblemRepository courseProblemRepository;
  private final DiscordWebhookService discordWebhookService;

  @Transactional
  public void createProblemsByCourse(Course course, CourseRequestDto courseRequestDto)
      throws IOException {
    List<Algorithm> algorithms =
        algorithmService.getAlgorithmsByIds(courseRequestDto.getAlgorithmIdList());
    List<Integer> rankList = generateRankList(course.getMinRank(), course.getMaxRank());
    List<Problem> scrapProblems = scrapProblems(course, algorithms, rankList);
    List<CourseProblem> courseProblemList =
        scrapProblems.stream()
            .map(
                problem ->
                    CourseProblem.builder()
                        .problem(problem) // 여기서 이미 Problem과 연결
                        .course(course)
                        .build())
            .toList();
    courseProblemRepository.saveAll(courseProblemList);
    course.addAllCourseProblems(courseProblemList);
    course.calculateAverageRank();
    course.updateRankRange();
    discordWebhookService.sendScrapResultEmbed(course, scrapProblems);
  }

  private List<Integer> generateRankList(int minRank, int maxRank) {
    return IntStream.rangeClosed(minRank, maxRank).boxed().collect(Collectors.toList());
  }

  @Transactional
  public List<Problem> scrapProblems(
      Course course, List<Algorithm> algorithms, List<Integer> rankList) throws IOException {
    String url = getProblemUrl(algorithms, rankList);
    List<Problem> problems = crawlProblems(url);
    if (problems.size() < course.getProblemCnt()) {
      throw new IllegalArgumentException("해당 조건에 해당하는 문제 수가 부족합니다. 알고리즘을 더 추가하거나 랭크의 범위를 넓히세요");
    }

    return saveAndSortProblems(problems.subList(0, course.getProblemCnt()));
  }

  @Transactional
  public List<Problem> crawlProblems(String url) throws IOException {
    Document document = Jsoup.connect(url).get();
    Elements rows = document.select("tbody tr");

    List<Integer> problemNumbers = extractProblemNumbers(rows);
    Collections.shuffle(problemNumbers);

    // 문제를 하나씩 확인하며 새로운 문제인지 확인합니다.
    return problemNumbers.stream()
        .map(
            problemNumber -> {
              // 먼저 problemId가 존재하는지 확인
              Optional<Problem> existingProblem =
                  problemService.findProblemByProblemId(problemNumber);

              return existingProblem.orElseGet(
                  () -> {
                    try {
                      String problemUrl = getProblemUrl(problemNumber);
                      String jsonString = fetchJsonFromUrl(problemUrl);
                      return parseProblem(jsonString);
                    } catch (Exception e) {
                      System.err.println(
                          "Error fetching problem " + problemNumber + ": " + e.getMessage());
                      return null;
                    }
                  });
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private String getProblemUrl(List<Algorithm> algorithms, List<Integer> rankList) {
    List<Integer> algorithmIdList = algorithms.stream().map(Algorithm::getAlgorithmId).toList();
    String algorithmIds =
        algorithmIdList.stream().map(Object::toString).collect(Collectors.joining(","));
    String tiers = rankList.stream().map(Object::toString).collect(Collectors.joining(","));

    return String.format(
        "https://www.acmicpc.net/problemset?sort=ac_desc&tier=%s&algo=%s&algo_if=or",
        tiers, algorithmIds);
  }

  @Transactional
  public List<Problem> saveAndSortProblems(List<Problem> problems) {
    problems.sort(Comparator.comparingInt(Problem::getRank));

    return problems.stream()
        .map(
            problem -> {
              problemAlgorithmRepository.saveAll(problem.getProblemAlgorithmList());
              return problemService.saveProblem(problem);
            })
        .collect(Collectors.toList());
  }

  private Problem parseProblem(String jsonString) {
    JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

    String titleKo = extractTitleKo(jsonObject); // 한국어 제목 추출
    if (titleKo == null) {
      throw new IllegalArgumentException("Korean title not found in JSON: " + jsonString);
    }
    int problemId = jsonObject.get("problemId").getAsInt();
    int rank = jsonObject.get("level").getAsInt();

    Problem problem = Problem.builder().title(titleKo).rank(rank).problemId(problemId).build();
    // ProblemTag 리스트를 생성하며 Problem 객체와 연결
    List<ProblemAlgorithm> problemAlgorithmList =
        extractAlgorithms(jsonObject).stream()
            .map(
                algorithm ->
                    ProblemAlgorithm.builder()
                        .problem(problem) // 여기서 이미 Problem과 연결
                        .algorithm(algorithm)
                        .build())
            .toList();
    problem.addAllProblemAlgorithms(problemAlgorithmList);
    return problem;
  }

  private List<Integer> extractProblemNumbers(Elements rows) {
    List<Integer> problemNumbers = new ArrayList<>();
    int count = 0;
    for (Element row : rows) {
      if (count >= 50) {
        break; // 100개에 도달하면 루프 종료
      }
      String problemIdText = row.select(".list_problem_id").text();
      int problemId = Integer.parseInt(problemIdText);
      problemNumbers.add(problemId);
      count++;
    }
    return problemNumbers;
  }

  private String extractTitleKo(JsonObject jsonObject) {
    if (jsonObject.has("titles")) {
      JsonArray titles = jsonObject.getAsJsonArray("titles");
      for (JsonElement titleElement : titles) {
        JsonObject titleObject = titleElement.getAsJsonObject();
        if ("ko".equals(titleObject.get("language").getAsString())) {
          return titleObject.get("title").getAsString();
        }
      }
    }
    // 한국어 제목을 찾지 못한 경우, null을 반환합니다.
    return null;
  }

  private List<Algorithm> extractAlgorithms(JsonObject jsonObject) {
    List<Algorithm> algorithmList = new ArrayList<>();
    JsonArray tagsArray = jsonObject.getAsJsonArray("tags");
    for (int i = 0; i < tagsArray.size(); i++) {
      JsonObject tagObject = tagsArray.get(i).getAsJsonObject();
      Integer algorithmId = tagObject.getAsJsonObject().get("bojTagId").getAsInt();
      JsonArray displayNames = tagObject.getAsJsonArray("displayNames");
      String koreanName = displayNames.get(0).getAsJsonObject().get("name").getAsString();
      String englishName = displayNames.get(1).getAsJsonObject().get("name").getAsString();

      Algorithm algorithm = getOrCreateAlgorithm(algorithmId, koreanName, englishName);
      algorithmList.add(algorithm);
    }
    return algorithmList;
  }

  private Algorithm getOrCreateAlgorithm(
      Integer algorithmId, String koreanName, String englishName) {
    return algorithmService.getOrCreateAlgorithm(algorithmId, koreanName, englishName);
  }

  private String getProblemUrl(int problemNumber) {
    return String.format("https://solved.ac/api/v3/problem/show?problemId=%d", problemNumber);
  }

  protected String fetchJsonFromUrl(String url) throws IOException {
    int maxRetries = 3; // 최대 3번까지 재시도
    int retryDelayMs = 10000; // 5초

    for (int attempt = 0; attempt < maxRetries; attempt++) {
      try {
        HttpURLConnection connection = createConnection(url);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
          return readResponse(connection);
        } else if (responseCode == 429) { // Too Many Requests
          // API 제한에 걸린 경우, 더 오래 기다립니다.
          Thread.sleep(retryDelayMs * 2);
        } else {
          System.out.println("HTTP Error: " + responseCode + " for URL: " + url);
        }
      } catch (IOException e) {
        System.out.println("Attempt " + (attempt + 1) + " failed: " + e.getMessage());
        if (attempt == maxRetries - 1) {
          throw e; // 마지막 시도에서 실패하면 예외를 던집니다.
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IOException("Request interrupted", e);
      }

      // 재시도 전 대기
      try {
        Thread.sleep(retryDelayMs);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IOException("Sleep interrupted", e);
      }
    }

    throw new IOException("Failed to fetch data after " + maxRetries + " attempts");
  }

  private HttpURLConnection createConnection(String url) throws IOException {
    URL apiUrl = new URL(url);
    HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty(HEADER_FIELD_NAME, HEADER_FIELD_VALUE);
    return connection;
  }

  private String readResponse(HttpURLConnection connection) throws IOException {
    StringBuilder response = new StringBuilder();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
    }
    return response.toString();
  }
}
