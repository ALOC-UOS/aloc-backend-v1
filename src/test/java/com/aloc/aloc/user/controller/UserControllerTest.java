package com.aloc.aloc.user.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.aloc.aloc.course.dto.response.CourseUserResponseDto;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.user.dto.request.UserRequestDto;
import com.aloc.aloc.user.dto.response.UserColorChangeResponseDto;
import com.aloc.aloc.user.dto.response.UserCourseResponseDto;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.service.facade.UserFacade;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock private UserFacade userFacade;
  @InjectMocks private UserController userController;
  
  private User mockUser;

  @BeforeEach
  void setUp() {
    mockUser = (User) User.withUsername("testUser")
        .password("password")
        .authorities("ROLE_USER")
        .build();
  }

  @Nested
  @DisplayName("유저 조회 API 테스트")
  class GetUsersTest {

    @Test
    @DisplayName("모든 유저 목록 조회 성공")
    void getUsers() {
      // given
      List<UserDetailResponseDto> mockUsers = List.of(
          createMockUserDetailResponseDto()
      );
      given(userFacade.getUsers()).willReturn(mockUsers);

      // when
      CustomApiResponse<List<UserDetailResponseDto>> response = userController.getUsers();

      // then
      assertThat(response.getIsSuccess()).isTrue();
      assertThat(response.getResult()).hasSize(1);
      verify(userFacade).getUsers();
    }
  }

  @Nested
  @DisplayName("인증된 유저 API 테스트")
  class AuthenticatedUserTest {

    @Test
    @DisplayName("로그인된 유저 정보 조회 성공")
    void getUser() {
      // given
      UserDetailResponseDto mockUserDetail = createMockUserDetailResponseDto();
      given(userFacade.getUser("testUser")).willReturn(mockUserDetail);

      // when
      CustomApiResponse<UserDetailResponseDto> response = userController.getUser(mockUser);

      // then
      assertThat(response.getIsSuccess()).isTrue();
      assertThat(response.getResult().getName()).isEqualTo("테스트유저");
      verify(userFacade).getUser("testUser");
    }

    @Test
    @DisplayName("유저 정보 업데이트 성공")
    void updateUser() {
      // given
      UserRequestDto requestDto = new UserRequestDto();
      UserDetailResponseDto responseDto = createMockUserDetailResponseDto();
      given(userFacade.updateUser("testUser", requestDto)).willReturn(responseDto);

      // when
      CustomApiResponse<UserDetailResponseDto> response = userController.updateUser(mockUser, requestDto);

      // then
      assertThat(response.getIsSuccess()).isTrue();
      verify(userFacade).updateUser("testUser", requestDto);
    }

    @Test
    @DisplayName("프로필 이미지 업데이트 성공")
    void updateUserProfileImage() throws FileUploadException {
      // given
      MultipartFile mockFile = mock(MultipartFile.class);
      UserDetailResponseDto responseDto = createMockUserDetailResponseDto();
      given(userFacade.updateUserProfileImage("testUser", mockFile)).willReturn(responseDto);

      // when
      CustomApiResponse<UserDetailResponseDto> response = userController.updateUserProfileImage(mockUser, mockFile);

      // then
      assertThat(response.getIsSuccess()).isTrue();
      verify(userFacade).updateUserProfileImage("testUser", mockFile);
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void withdraw() {
      // given
      doNothing().when(userFacade).withdraw("testUser");

      // when
      userController.withdraw(mockUser);

      // then
      verify(userFacade).withdraw("testUser");
    }

    @Test
    @DisplayName("프로필 색상 변경 성공")
    void changeColor() {
      // given
      UserColorChangeResponseDto responseDto = UserColorChangeResponseDto.builder()
          .userCoin(50)
          .build();
      given(userFacade.changeColor("testUser")).willReturn(responseDto);

      // when
      CustomApiResponse<UserColorChangeResponseDto> response = userController.changeColor(mockUser);

      // then
      assertThat(response.getIsSuccess()).isTrue();
      assertThat(response.getResult().getUserCoin()).isEqualTo(50);
      verify(userFacade).changeColor("testUser");
    }
  }

  @Nested
  @DisplayName("유저 코스 관리 API 테스트")
  class UserCourseTest {

    @Test
    @DisplayName("유저 코스 목록 조회 성공")
    void getUserCourses() {
      // given
      List<UserCourseResponseDto> mockCourses = List.of(
          createMockUserCourseResponseDto(1L)
      );
      given(userFacade.getUserCourses("testUser")).willReturn(mockCourses);

      // when
      CustomApiResponse<List<UserCourseResponseDto>> response = userController.getUserCourses(mockUser);

      // then
      assertThat(response.getIsSuccess()).isTrue();
      assertThat(response.getResult()).hasSize(1);
      verify(userFacade).getUserCourses("testUser");
    }

    @Test
    @DisplayName("유저 코스 선택 성공")
    void createUserCourse() {
      // given
      Long courseId = 1L;
      CourseUserResponseDto responseDto = createMockCourseUserResponseDto();
      given(userFacade.createUserCourse(courseId, "testUser")).willReturn(responseDto);

      // when
      CustomApiResponse<CourseUserResponseDto> response = userController.createUserCourse(courseId, mockUser);

      // then
      assertThat(response.getIsSuccess()).isTrue();
      verify(userFacade).createUserCourse(courseId, "testUser");
    }

    @Test
    @DisplayName("유저 코스 포기 성공")
    void closeUserCourse() {
      // given
      Long courseId = 1L;
      CourseUserResponseDto responseDto = createMockCourseUserResponseDto();
      given(userFacade.closeUserCourse(courseId, "testUser")).willReturn(responseDto);

      // when
      CustomApiResponse<CourseUserResponseDto> response = userController.closeUserCourse(courseId, mockUser);

      // then
      assertThat(response.getIsSuccess()).isTrue();
      verify(userFacade).closeUserCourse(courseId, "testUser");
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

  private UserCourseResponseDto createMockUserCourseResponseDto(Long courseId) {
    return UserCourseResponseDto.builder()
        .id(courseId)
        .title("테스트 코스")
        .problemCnt(5)
        .todayProblemId(1001)
        .build();
  }

  private CourseUserResponseDto createMockCourseUserResponseDto() {
    return CourseUserResponseDto.builder()
        .id(1L)
        .build();
  }
}