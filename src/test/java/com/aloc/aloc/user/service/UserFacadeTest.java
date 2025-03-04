package com.aloc.aloc.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserFacadeTest {

  @Mock private UserSortingService userSortingService;

  @Mock private UserService userService;

  @InjectMocks private UserFacade userFacade;

  @Mock private UserMapper userMapper;

  private List<User> testUsers;
  private List<UserDetailResponseDto> expectedDtos;

  @BeforeEach
  public void setUp() {

    MockitoAnnotations.openMocks(this);
    User user1 =
        new User(
            "user1",
            "baekjoon1",
            "github1",
            "20210001",
            "password",
            "adminDiscord",
            1,
            "adminNotion",
            Course.FULL,
            "oauthId",
            "userId",
            "nickname",
            "profileImageUrl");
    User user2 =
        new User(
            "user",
            "userBaekjoon",
            "userGithub",
            "20210002",
            "password",
            "userDiscord",
            2,
            "userNotion",
            Course.FULL,
            "oauthId",
            "userId",
            "nickname",
            "profileImageUrl");

    testUsers = Arrays.asList(user1, user2);

    expectedDtos =
        Arrays.asList(
            UserDetailResponseDto.builder()
                .username("user1")
                .githubId("github1")
                .baekjoonId("baekjoon1")
                .profileColor("1")
                .studentId("20210001")
                .rank(1)
                .coin(0)
                .solvedCount(0)
                .unsolvedCount(0)
                .todaySolved(false)
                .colorCategory("category")
                .color1("color1")
                .color2("color2")
                .color3("color3")
                .color4("color4")
                .color5("color5")
                .degree(0)
                .createdAt(null)
                .build(),
            UserDetailResponseDto.builder()
                .username("user")
                .githubId("userGithub")
                .baekjoonId("userBaekjoon")
                .profileColor("2")
                .studentId("20210002")
                .rank(2)
                .coin(0)
                .solvedCount(0)
                .unsolvedCount(0)
                .todaySolved(false)
                .colorCategory("category")
                .color1("color1")
                .color2("color2")
                .color3("color3")
                .color4("color4")
                .color5("color5")
                .degree(0)
                .createdAt(null)
                .build());
  }

  @Test
  @DisplayName("활성 사용자 목록 조회 및 정렬 성공")
  void getUsersSuccess() {
    // Arrange
    when(userService.getActiveUsers()).thenReturn(testUsers);
    when(userSortingService.sortUserList(testUsers)).thenReturn(testUsers);

    when(userMapper.mapToUserDetailResponseDto(any(User.class)))
        .thenReturn(expectedDtos.get(0), expectedDtos.get(1));

    // Act
    List<UserDetailResponseDto> result = userFacade.getUsers();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(expectedDtos, result);

    verify(userService).getActiveUsers();
    verify(userSortingService).sortUserList(testUsers);
    verify(userMapper, times(2)).mapToUserDetailResponseDto(any(User.class));
  }

  @Test
  @DisplayName("활성 사용자가 없을 때 빈 리스트 반환")
  void getUsersNoActiveUsers() {
    // Arrange
    when(userService.getActiveUsers()).thenReturn(List.of());

    // Act
    List<UserDetailResponseDto> result = userFacade.getUsers();

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(userService).getActiveUsers();
    verify(userSortingService, never()).sortUserList(any());
    verify(userMapper, never()).mapToUserDetailResponseDto(any(User.class));
  }

  @Test
  @DisplayName("예외 발생 시 적절한 처리")
  void getUsersExceptionHandling() {
    // Arrange
    when(userService.getActiveUsers()).thenThrow(new RuntimeException("Database error"));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> userFacade.getUsers());

    verify(userService).getActiveUsers();
    verify(userSortingService, never()).sortUserList(any());
    verify(userMapper, never()).mapToUserDetailResponseDto(any(User.class));
  }
}
