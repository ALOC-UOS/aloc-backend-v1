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
  @DisplayName("유저 랭킹 정렬 테스트")
  class SortUserListTest {

    @Test
    @DisplayName("같은 10의 자리 내에서 1의 자리를 역순으로 정렬")
    void sortUserListSameTensDigitReverseOnesDigit() {
      // given
      User user23 = TestFixture.getMockUserByOauthId("user23");
      user23.setRank(23); // (2, -3)
      User user27 = TestFixture.getMockUserByOauthId("user27");
      user27.setRank(27); // (2, -7)
      User user21 = TestFixture.getMockUserByOauthId("user21");
      user21.setRank(21); // (2, -1)

      List<User> unsortedUsers = List.of(user23, user27, user21);

      // when
      List<User> sortedUsers = userSortingService.sortUserList(unsortedUsers);

      // then
      assertThat(sortedUsers).hasSize(3);
      assertThat(sortedUsers.get(0).getRank()).isEqualTo(21); // (2, -1)
      assertThat(sortedUsers.get(1).getRank()).isEqualTo(23); // (2, -3)
      assertThat(sortedUsers.get(2).getRank()).isEqualTo(27); // (2, -7)
    }

    @Test
    @DisplayName("10의 자리가 다르면 10의 자리를 먼저 정렬")
    void sortUserListDifferentTensDigit() {
      // given
      User user19 = TestFixture.getMockUserByOauthId("user19");
      user19.setRank(19); // (1, -9)
      User user21 = TestFixture.getMockUserByOauthId("user21");
      user21.setRank(21); // (2, -1)
      User user18 = TestFixture.getMockUserByOauthId("user18");
      user18.setRank(18); // (1, -8)

      List<User> unsortedUsers = List.of(user19, user21, user18);

      // when
      List<User> sortedUsers = userSortingService.sortUserList(unsortedUsers);

      // then
      assertThat(sortedUsers).hasSize(3);
      assertThat(sortedUsers.get(0).getRank()).isEqualTo(21); // (2, -1)
      assertThat(sortedUsers.get(1).getRank()).isEqualTo(18); // (1, -8)
      assertThat(sortedUsers.get(2).getRank()).isEqualTo(19); // (1, -9)
    }

    @Test
    @DisplayName("null 랭크는 0으로 처리되어 최하위로 정렬됨")
    void sortUserListWithNullRank() {
      // given
      User user25 = TestFixture.getMockUserByOauthId("user25");
      user25.setRank(25);
      User userNull = TestFixture.getMockUserByOauthId("userNull");
      userNull.setRank(null); // null은 0으로 처리
      User user15 = TestFixture.getMockUserByOauthId("user15");
      user15.setRank(15);

      List<User> unsortedUsers = List.of(user25, userNull, user15);

      // when
      List<User> sortedUsers = userSortingService.sortUserList(unsortedUsers);

      // then
      assertThat(sortedUsers).hasSize(3);
      assertThat(sortedUsers.get(0).getRank()).isEqualTo(25);
      assertThat(sortedUsers.get(1).getRank()).isEqualTo(15);
      assertThat(sortedUsers.get(2).getRank()).isNull(); // null은 맨 마지막
    }

    @Test
    @DisplayName("다양한 랭크 조합에서도 정렬 로직이 올바르게 동작")
    void sortUserListComplexRankCombination() {
      // given
      User user5 = TestFixture.getMockUserByOauthId("user5");
      user5.setRank(5); // (0, -5)

      User user15 = TestFixture.getMockUserByOauthId("user15");
      user15.setRank(15); // (1, -5)

      User userNull = TestFixture.getMockUserByOauthId("userNull");
      userNull.setRank(null); // (0, 0)

      User user12 = TestFixture.getMockUserByOauthId("user12");
      user12.setRank(12); // (1, -2)

      List<User> unsortedUsers = List.of(user5, user15, userNull, user12);

      // when
      List<User> sortedUsers = userSortingService.sortUserList(unsortedUsers);

      // then - (1, -2) > (1, -5) > (0, 0) > (0, -5) 순서
      assertThat(sortedUsers).hasSize(4);
      assertThat(sortedUsers.get(0).getRank()).isEqualTo(12); // (1, -2)
      assertThat(sortedUsers.get(1).getRank()).isEqualTo(15); // (1, -5)
      assertThat(sortedUsers.get(2).getRank()).isNull(); // (0, 0) - null은 0으로 처리
      assertThat(sortedUsers.get(3).getRank()).isEqualTo(5); // (0, -5)
    }
  }
}
