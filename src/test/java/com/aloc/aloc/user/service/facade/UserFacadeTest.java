package com.aloc.aloc.user.service.facade;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.course.service.UserCourseService;
import com.aloc.aloc.global.apipayload.exception.NoContentException;
import com.aloc.aloc.global.image.ImageService;
import com.aloc.aloc.problem.service.UserCourseProblemService;
import com.aloc.aloc.profilebackgroundcolor.service.ProfileBackgroundColorService;
import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.user.dto.request.UserRequestDto;
import com.aloc.aloc.user.dto.response.UserColorChangeResponseDto;
import com.aloc.aloc.user.dto.response.UserCourseResponseDto;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.service.UserService;
import com.aloc.aloc.user.service.UserSortingService;
import com.aloc.aloc.user.service.mapper.UserMapper;
import com.aloc.aloc.usercourse.entity.UserCourse;
import java.util.ArrayList;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

  @Mock private UserSortingService userSortingService;
  @Mock private UserMapper userMapper;
  @Mock private UserService userService;
  @Mock private UserCourseService userCourseService;
  @Mock private UserCourseProblemService userCourseProblemService;

  @InjectMocks private UserFacade userFacade;

  @Nested
  @DisplayName("유저 목록 조회 테스트")
  class GetUsersTest {

    @Test
    @DisplayName("활성 유저 목록 조회 성공")
    void getUsers() {
      // given
      List<User> mockUsers = List.of(TestFixture.getMockNewUser());
      List<User> sortedUsers = List.of(TestFixture.getMockNewUser());
      UserDetailResponseDto mockDto = createMockUserDetailResponseDto();

      given(userService.getActiveUsers()).willReturn(mockUsers);
      given(userSortingService.sortUserList(mockUsers)).willReturn(sortedUsers);
      given(userMapper.mapToUserDetailResponseDto(any(User.class))).willReturn(mockDto);

      // when
      List<UserDetailResponseDto> result = userFacade.getUsers();

      // then
      assertThat(result).hasSize(1);
      verify(userService).getActiveUsers();
      verify(userSortingService).sortUserList(mockUsers);
      verify(userMapper).mapToUserDetailResponseDto(any(User.class));
    }

    @Test
    @DisplayName("활성 유저가 없을 때 NoContentException 발생")
    void getUsers_NoUsers() {
      // given
      given(userService.getActiveUsers()).willReturn(new ArrayList<>());

      // when & then
      assertThatThrownBy(() -> userFacade.getUsers())
          .isInstanceOf(NoContentException.class)
          .hasMessage("조회 가능한 유저가 없습니다.");
    }

    @Test
    @DisplayName("모든 유저 목록 조회 성공")
    void getAllUsers() {
      // given
      List<User> mockUsers = List.of(TestFixture.getMockNewUser());
      List<User> sortedUsers = List.of(TestFixture.getMockNewUser());

      given(userService.findAllUsers()).willReturn(mockUsers);
      given(userSortingService.sortUserList(mockUsers)).willReturn(sortedUsers);

      // when
      List<User> result = userFacade.getAllUsers();

      // then
      assertThat(result).hasSize(1);
      verify(userService).findAllUsers();
      verify(userSortingService).sortUserList(mockUsers);
    }
  }

  @Nested
  @DisplayName("개별 유저 조회 테스트")
  class GetUserTest {

    @Test
    @DisplayName("유저 상세 정보 조회 성공")
    void getUser() {
      // given
      String oauthId = "testOauth";
      User mockUser = TestFixture.getMockUserByOauthId(oauthId);
      UserDetailResponseDto mockDto = createMockUserDetailResponseDto();

      given(userService.getUser(oauthId)).willReturn(mockUser);
      given(userMapper.mapToUserDetailResponseDto(mockUser)).willReturn(mockDto);

      // when
      UserDetailResponseDto result = userFacade.getUser(oauthId);

      // then
      assertThat(result).isNotNull();
      verify(userService).getUser(oauthId);
      verify(userMapper).mapToUserDetailResponseDto(mockUser);
    }
  }

  @Nested
  @DisplayName("유저 코스 관련 테스트")
  class UserCourseTest {

    @Test
    @DisplayName("유저 진행 중인 코스 목록 조회 성공")
    void getUserCourses() {
      // given
      String oauthId = "testOauth";
      User mockUser = TestFixture.getMockUserByOauthId(oauthId);
      UserCourse mockUserCourse = createMockUserCourse();
      List<UserCourse> userCourses = List.of(mockUserCourse);

      given(userService.getUser(oauthId)).willReturn(mockUser);
      given(userCourseService.getUserCoursesInProcessByUser(mockUser)).willReturn(userCourses);
      given(userCourseProblemService.getTodayProblemId(anyList())).willReturn(1001);
      given(userCourseProblemService.mapToProblemResponseDto(anyList())).willReturn(new ArrayList<>());

      // when
      List<UserCourseResponseDto> result = userFacade.getUserCourses(oauthId);

      // then
      assertThat(result).hasSize(1);
      verify(userService).getUser(oauthId);
      verify(userCourseService).getUserCoursesInProcessByUser(mockUser);
    }
  }

  @Nested
  @DisplayName("유저 정보 업데이트 테스트")
  class UpdateUserTest {

    @Test
    @DisplayName("신규 유저의 백준 ID 업데이트 성공")
    void updateUser_NewUserWithBaekjoonId() {
      // given
      String oauthId = "testOauth";
      User mockUser = TestFixture.getMockUserByOauthId(oauthId);
      mockUser.setAuthority(Authority.ROLE_NEW_USER);
      
      UserRequestDto requestDto = new UserRequestDto();
      UserDetailResponseDto mockResponse = createMockUserDetailResponseDto();

      given(userService.getUser(oauthId)).willReturn(mockUser);
      given(userMapper.mapToUserDetailResponseDto(mockUser)).willReturn(mockResponse);

      // when
      UserDetailResponseDto result = userFacade.updateUser(oauthId, requestDto);

      // then
      assertThat(result).isNotNull();
      verify(userService).getUser(oauthId);
      verify(userService).saveUser(mockUser);
      verify(userMapper).mapToUserDetailResponseDto(mockUser);
    }

    @Test
    @DisplayName("프로필 이미지 업데이트 성공")
    void updateUserProfileImage() throws FileUploadException {
      // given
      String oauthId = "testOauth";
      User mockUser = TestFixture.getMockUserByOauthId(oauthId);
      UserDetailResponseDto mockResponse = createMockUserDetailResponseDto();

      given(userService.getUser(oauthId)).willReturn(mockUser);
      given(userMapper.mapToUserDetailResponseDto(mockUser)).willReturn(mockResponse);

      // when
      UserDetailResponseDto result = userFacade.updateUserProfileImage(oauthId, null);

      // then
      assertThat(result).isNotNull();
      verify(userService).getUser(oauthId);
      verify(userMapper).mapToUserDetailResponseDto(mockUser);
    }
  }

  @Nested
  @DisplayName("기타 기능 테스트")
  class OtherFunctionsTest {

    @Test
    @DisplayName("회원 탈퇴 성공")
    void withdraw() {
      // given
      String oauthId = "testOauth";
      User mockUser = TestFixture.getMockUserByOauthId(oauthId);
      List<UserCourse> userCourses = new ArrayList<>();

      given(userService.getUser(oauthId)).willReturn(mockUser);
      given(userCourseService.getUserCoursesByUser(mockUser)).willReturn(userCourses);
      doNothing().when(userCourseService).deleteUserCourses(userCourses);
      doNothing().when(userService).saveUser(mockUser);

      // when
      userFacade.withdraw(oauthId);

      // then
      assertThat(mockUser.getAuthority()).isEqualTo(Authority.ROLE_DELETE);
      assertThat(mockUser.getDeletedAt()).isNotNull();
      verify(userService).getUser(oauthId);
      verify(userCourseService).deleteUserCourses(userCourses);
      verify(userService).saveUser(mockUser);
    }

    @Test
    @DisplayName("프로필 색상 변경 성공")
    void changeColor() {
      // given
      String oauthId = "testOauth";
      User mockUser = TestFixture.getMockUserByOauthId(oauthId);
      UserColorChangeResponseDto mockResponse = UserColorChangeResponseDto.builder()
          .userCoin(50)
          .build();

      given(userService.getUser(oauthId)).willReturn(mockUser);
      given(userService.changeColor(mockUser)).willReturn(mockResponse);

      // when
      UserColorChangeResponseDto result = userFacade.changeColor(oauthId);

      // then
      assertThat(result).isNotNull();
      assertThat(result.getUserCoin()).isEqualTo(50);
      verify(userService).getUser(oauthId);
      verify(userService).changeColor(mockUser);
    }
  }

  private UserDetailResponseDto createMockUserDetailResponseDto() {
    return UserDetailResponseDto.builder()
        .name("테스트유저")
        .baekjoonId("testBaekjoon")
        .rank(25)
        .coin(100)
        .solvedCount(50)
        .consecutiveSolvedDays(5)
        .isTodaySolved(false)
        .build();
  }

  private Course createMockCourse() {
    return Course.builder()
        .title("테스트 코스")
        .description("테스트 설명")
        .courseType(CourseType.DAILY)
        .problemCnt(5)
        .duration(7)
        .build();
  }

  private UserCourse createMockUserCourse() {
    return UserCourse.builder()
        .course(createMockCourse())
        .user(TestFixture.getMockNewUser())
        .userCourseState(UserCourseState.IN_PROGRESS)
        .userCourseProblemList(new ArrayList<>())
        .build();
  }
}