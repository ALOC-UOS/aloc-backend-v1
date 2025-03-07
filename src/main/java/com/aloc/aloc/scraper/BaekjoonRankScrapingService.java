package com.aloc.aloc.scraper;

import com.aloc.aloc.global.apipayload.exception.ScrapException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.stereotype.Service;

@Service
public class BaekjoonRankScrapingService {
  private static final String BAEKJOON_API_URL = "https://solved.ac/api/v3/user/show?handle=";

  private static final String HEADER_FIELD_NAME = "User-Agent";
  private static final String HEADER_FIELD_VALUE =
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
          + "(KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3";

  public int extractBaekjoonRank(String baekjoonId) {
    try {
      String jsonResponse = fetchBaekjoonData(baekjoonId);
      int rank = parseRankFromJson(jsonResponse);
      return calculateTierValue(rank);
    } catch (Exception e) {
      throw new IllegalArgumentException("잘못된 백준 아이디입니다.");
    }
  }

  private String fetchBaekjoonData(String baekjoonId) throws ScrapException {
    try {
      URL url = new URL(BAEKJOON_API_URL + baekjoonId);
      HttpURLConnection connection = getConnection(url);

      // API를 호출하여 데이터를 받아옵니다.
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        return readResponse(connection);
      } else {
        throw new ScrapException("백준 API 호출 실패. 응답 코드: " + responseCode);
      }
    } catch (Exception e) {
      throw new ScrapException("백준 데이터 가져오기 실패", e);
    }
  }

  private HttpURLConnection getConnection(URL url) throws Exception {
    // HttpURLConnection 설정
    try {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty(HEADER_FIELD_NAME, HEADER_FIELD_VALUE);
      return connection;
    } catch (Exception e) {
      throw new Exception("HttpURLConnection 설정 중 오류 발생");
    }
  }

  private String readResponse(HttpURLConnection connection) throws ScrapException {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
      return response.toString();
    } catch (IOException e) {
      throw new ScrapException("응답 읽기 실패", e);
    }
  }

  private int parseRankFromJson(String json) throws ScrapException {
    try {
      int tierIndex = json.indexOf("\"tier\":") + 7;
      int commaIndex = json.indexOf(",", tierIndex);
      return Integer.parseInt(json.substring(tierIndex, commaIndex).trim());
    } catch (Exception e) {
      throw new ScrapException("JSON에서 랭크 파싱 실패", e);
    }
  }

  private int calculateTierValue(int rank) {
    return 10 * (1 + (rank - 1) / 5) + (5 - (rank - 1) % 5);
  }
}
