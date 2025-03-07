package com.aloc.aloc.scraper;

import com.aloc.aloc.algorithm.entity.Algorithm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class AlgorithmScrapingService {
  private static final String ALGORITHM_URL = "https://www.acmicpc.net/problem/tags";

  public List<Algorithm> scrapAlgorithms() {
    List<Algorithm> algorithms = new ArrayList<>();

    try {
      // 웹페이지 크롤링
      Document document = Jsoup.connect(ALGORITHM_URL).get();

      // 알고리즘 태그 리스트 선택 (table 안의 <tr> 요소)
      Elements rows = document.select("table.table-striped tbody tr");

      for (Element row : rows) {
        Elements tds = row.select("td"); // 각 <td> 요소들 가져오기

        if (tds.size() >= 4) { // 4개 이상일 때만 진행
          Element koreanTag = tds.get(0).selectFirst("a[href^=/problem/tag/]"); // 한글 태그
          Element englishTag = tds.get(1).selectFirst("a[href^=/problem/tag/]"); // 영어 태그

          if (koreanTag != null && englishTag != null) {
            String koreanName = koreanTag.text().trim();
            String englishName = englishTag.text().trim();
            int algorithmId = Integer.parseInt(koreanTag.attr("href").split("/")[3]); // URL에서 ID 추출

            algorithms.add(
                Algorithm.builder()
                    .algorithmId(algorithmId)
                    .koreanName(koreanName)
                    .englishName(englishName)
                    .build());
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return algorithms;
  }
}
