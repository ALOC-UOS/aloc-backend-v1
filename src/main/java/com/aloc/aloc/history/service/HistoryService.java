package com.aloc.aloc.history.service;

import com.aloc.aloc.history.History;
import com.aloc.aloc.history.dto.response.HistoryContentDto;
import com.aloc.aloc.history.dto.response.HistoryResponseDto;
import com.aloc.aloc.history.repository.HistoryRepository;
import com.aloc.aloc.user.entity.User;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryService {
  private final HistoryRepository historyRepository;
  private static final List<String> WEEKDAY_LIST = Arrays.asList("일", "월", "화", "수", "목", "금", "토");
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 e");

  public List<HistoryResponseDto> getHistories() {
    List<History> histories = historyRepository.findByOrderByCreatedAtDesc();

    Map<String, List<History>> groupedHistories = groupingHistoryByDate(histories);

    return groupedHistories.entrySet().stream()
        .sorted(Map.Entry.comparingByKey(Collections.reverseOrder()))
        .map(
            entry -> {
              String date = entry.getKey();
              String formattedDate =
                  date.substring(0, date.length() - 1)
                      + WEEKDAY_LIST.get(Integer.parseInt(date.substring(date.length() - 1)) - 1)
                      + "요일";
              List<HistoryContentDto> contentDtos = HistoryContentDto.listOf(entry.getValue());
              return new HistoryResponseDto(formattedDate, contentDtos);
            })
        .collect(Collectors.toList());
  }

  private Map<String, List<History>> groupingHistoryByDate(List<History> histories) {
    return histories.stream()
        .collect(
            Collectors.groupingBy(
                history -> history.getCreatedAt().format(DATE_FORMATTER),
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list ->
                        list.stream()
                            .sorted(Comparator.comparing(History::getCreatedAt).reversed())
                            .collect(Collectors.toList()))));
  }

  public void addHistory(User user, String icon, Integer rank) {
    historyRepository.save(
        History.builder().user(user).username(user.getName()).icon(icon).rank(rank).build());
  }
}
