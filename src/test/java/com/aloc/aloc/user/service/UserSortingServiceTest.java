package com.aloc.aloc.user.service;

import static org.assertj.core.api.Assertions.*;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.user.entity.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserSortingServiceTest {

  @InjectMocks private UserSortingService userSortingService;

  @Nested
  @DisplayName("유저 목록 정렬 테스트")
  class SortUserListTest {

    @Test
    @DisplayName("랭크 기준으로 정렬 - 높은 랭크 순서")
    void sortUserList_ByRankDescending() {
      // given
      User user1 = TestFixture.getMockUserByOauthId("user1");
      user1.setRank(25); // 높은 랭크

      User user2 = TestFixture.getMockUserByOauthId("user2");
      user2.setRank(15); // 낮은 랭크

      User user3 = TestFixture.getMockUserByOauthId("user3");
      user3.setRank(30); // 가장 높은 랭크

      List<User> unsortedUsers = List.of(user1, user2, user3);

      // when
      List<User> sortedUsers = userSortingService.sortUserList(unsortedUsers);

      // then
      assertThat(sortedUsers).hasSize(3);
      assertThat(sortedUsers.get(0).getRank()).isEqualTo(30); // 가장 높은 랭크
      assertThat(sortedUsers.get(1).getRank()).isEqualTo(25);
      assertThat(sortedUsers.get(2).getRank()).isEqualTo(15); // 가장 낮은 랭크
    }

    @Test
    @DisplayName("null 랭크 유저는 0으로 처리되어 가장 뒤로 정렬")
    void sortUserList_WithNullRank() {
      // given
      User user1 = TestFixture.getMockUserByOauthId("user1");
      user1.setRank(25);

      User user2 = TestFixture.getMockUserByOauthId("user2");
      user2.setRank(null); // null 랭크

      User user3 = TestFixture.getMockUserByOauthId("user3");
      user3.setRank(15);

      List<User> unsortedUsers = List.of(user1, user2, user3);

      // when
      List<User> sortedUsers = userSortingService.sortUserList(unsortedUsers);

      // then
      assertThat(sortedUsers).hasSize(3);
      assertThat(sortedUsers.get(0).getRank()).isEqualTo(25);
      assertThat(sortedUsers.get(1).getRank()).isEqualTo(15);
      assertThat(sortedUsers.get(2).getRank()).isNull(); // null 랭크는 마지막
    }

    @Test
    @DisplayName("같은 십의 자리 랭크 내에서 일의 자리 역순 정렬")
    void sortUserList_SameTensDigitReverseOnesDigit() {
      // given
      User user1 = TestFixture.getMockUserByOauthId("user1");
      user1.setRank(23); // (2, -3)

      User user2 = TestFixture.getMockUserByOauthId("user2");
      user2.setRank(27); // (2, -7)

      User user3 = TestFixture.getMockUserByOauthId("user3");
      user3.setRank(21); // (2, -1)

      List<User> unsortedUsers = List.of(user1, user2, user3);

      // when
      List<User> sortedUsers = userSortingService.sortUserList(unsortedUsers);

      // then
      assertThat(sortedUsers).hasSize(3);
      // 정렬 로직: (rank/10, -rank%10) 역순
      // (2, -1) > (2, -3) > (2, -7) 순서로 정렬될 것
      assertThat(sortedUsers.get(0).getRank()).isEqualTo(21); // (2, -1)
      assertThat(sortedUsers.get(1).getRank()).isEqualTo(23); // (2, -3)
      assertThat(sortedUsers.get(2).getRank()).isEqualTo(27); // (2, -7)
    }

    @Test
    @DisplayName("십의 자리가 다르면 십의 자리 우선 정렬")
    void sortUserList_DifferentTensDigit() {
      // given
      User user1 = TestFixture.getMockUserByOauthId("user1");
      user1.setRank(19); // (1, -9)

      User user2 = TestFixture.getMockUserByOauthId("user2");
      user2.setRank(21); // (2, -1)

      User user3 = TestFixture.getMockUserByOauthId("user3");
      user3.setRank(18); // (1, -8)

      List<User> unsortedUsers = List.of(user1, user2, user3);

      // when
      List<User> sortedUsers = userSortingService.sortUserList(unsortedUsers);

      // then
      assertThat(sortedUsers).hasSize(3);
      // 정렬 로직: (rank/10, -rank%10) 역순
      // (2, -1) > (1, -8) > (1, -9) 순서로 정렬
      assertThat(sortedUsers.get(0).getRank()).isEqualTo(21); // (2, -1)
      assertThat(sortedUsers.get(1).getRank()).isEqualTo(18); // (1, -8)
      assertThat(sortedUsers.get(2).getRank()).isEqualTo(19); // (1, -9)
    }

    @Test
    @DisplayName("빈 리스트 정렬")
    void sortUserList_EmptyList() {
      // given
      List<User> emptyList = List.of();

      // when
      List<User> sortedUsers = userSortingService.sortUserList(emptyList);

      // then
      assertThat(sortedUsers).isEmpty();
    }

    @Test
    @DisplayName("단일 유저 정렬")
    void sortUserList_SingleUser() {
      // given
      User singleUser = TestFixture.getMockUserByOauthId("singleUser");
      singleUser.setRank(25);
      List<User> singleUserList = List.of(singleUser);

      // when
      List<User> sortedUsers = userSortingService.sortUserList(singleUserList);

      // then
      assertThat(sortedUsers).hasSize(1);
      assertThat(sortedUsers.get(0).getRank()).isEqualTo(25);
    }

    @Test
    @DisplayName("복잡한 랭크 조합 정렬 테스트")
    void sortUserList_ComplexRankCombination() {
      // given
      User user1 = TestFixture.getMockUserByOauthId("user1");
      user1.setRank(5); // (0, -5)

      User user2 = TestFixture.getMockUserByOauthId("user2");
      user2.setRank(15); // (1, -5)

      User user3 = TestFixture.getMockUserByOauthId("user3");
      user3.setRank(null); // null -> (0, 0)

      User user4 = TestFixture.getMockUserByOauthId("user4");
      user4.setRank(12); // (1, -2)

      User user5 = TestFixture.getMockUserByOauthId("user5");
      user5.setRank(8); // (0, -8)

      List<User> unsortedUsers = List.of(user1, user2, user3, user4, user5);

      // when
      List<User> sortedUsers = userSortingService.sortUserList(unsortedUsers);

      // then
      assertThat(sortedUsers).hasSize(5);
      // 정렬 로직: (rank/10, -rank%10) 역순
      // 실제 정렬 결과: (1, -2) > (1, -5) > (0, 0) > (0, -5) > (0, -8)
      assertThat(sortedUsers.get(0).getRank()).isEqualTo(12); // (1, -2)
      assertThat(sortedUsers.get(1).getRank()).isEqualTo(15); // (1, -5)
      assertThat(sortedUsers.get(2).getRank()).isNull();      // null -> (0, 0)
      assertThat(sortedUsers.get(3).getRank()).isEqualTo(5);  // (0, -5)
      assertThat(sortedUsers.get(4).getRank()).isEqualTo(8);  // (0, -8)
    }
  }
}