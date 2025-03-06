package com.aloc.aloc.history.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aloc.aloc.history.History;
import com.aloc.aloc.history.dto.response.HistoryResponseDto;
import com.aloc.aloc.history.repository.HistoryRepository;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class HistoryServiceTest {

  @Mock private HistoryRepository historyRepository;

  @InjectMocks private HistoryService historyService;

  private List<History> histories;
  private User user;

  @BeforeEach
  void setUp() {
    histories = new ArrayList<>();
    History history1 = new History(1L, null, "홍길동", "plusMember", null);
    History history2 = new History(2L, null, "홍길동", "changeRank", 33);
    History history3 = new History(3L, null, "홍길동", "changeRank", 32);
    history1.setCreatedAt(LocalDateTime.now().minusDays(1));
    history2.setCreatedAt(LocalDateTime.now().minusDays(1));
    history3.setCreatedAt(LocalDateTime.now());

    histories.add(history1);
    histories.add(history2);
    histories.add(history3);

    user =
        new User(
            "username",
            "userBaekjoon",
            "serGithub",
            "20210001",
            "password",
            "userDiscord",
            1,
            "userNotion",
            Course.FULL,
            "oauthId",
            "userId",
            "nickname",
            "profileImageUrl");
  }

  @Test
  @DisplayName("모든 히스토리 목록 그룹화 하여 최신순으로 정렬")
  void getHistories() {
    when(historyRepository.findByOrderByCreatedAtDesc()).thenReturn(histories);

    List<HistoryResponseDto> result = historyService.getHistories();

    assertThat(result).hasSize(2);
    HistoryResponseDto history1 = result.get(0);
    assertThat(history1).isNotNull();
    assertThat(history1.getDate()).isNotNull();
    assertThat(history1.getContents().get(0).getRank()).isEqualTo(32);

    HistoryResponseDto history2 = result.get(1);
    assertThat(history2).isNotNull();
    assertThat(history2.getDate()).isNotNull();
    assertThat(history2.getContents().get(0).getRank()).isEqualTo(33);
  }

  @Test
  @DisplayName("히스토리 추가하기 - 멤버 추가")
  void addHistory() {
    String icon = "plusMember";

    historyService.addHistory(user, icon, null);

    ArgumentCaptor<History> captor = ArgumentCaptor.forClass(History.class);
    verify(historyRepository).save(captor.capture());
    History history = captor.getValue();
    assertEquals(user, history.getUser());
    assertEquals(user.getUsername(), history.getUsername());
    assertEquals(icon, history.getIcon());
    assertNull(history.getRank());
  }
}
