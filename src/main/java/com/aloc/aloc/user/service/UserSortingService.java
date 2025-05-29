package com.aloc.aloc.user.service;

import com.aloc.aloc.user.entity.User;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserSortingService {

  public List<User> sortUserList(List<User> userList) {
    return userList.stream()
        .sorted(Comparator.comparing(this::sortByRank, Comparator.reverseOrder())) // 랭크가 높은 사람 먼저
        .collect(Collectors.toList());
  }

  private Pair<Integer, Integer> sortByRank(User user) {
    Integer rank = user.getRank();
    if (rank == null) {
      rank = 0;
    }
    return new Pair<>(rank / 10, -rank % 10);
  }

  private record Pair<T extends Comparable<T>, U extends Comparable<U>>(T first, U second)

      implements Comparable<Pair<T, U>> {

    @Override
    public int compareTo(Pair<T, U> other) {
      int firstComparison = this.first.compareTo(other.first);
      if (firstComparison != 0) {
        return firstComparison;
      }
      return this.second.compareTo(other.second);
    }
  }
}
