package com.aloc.aloc.problem.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.enums.UserCourseProblemStatus;
import com.aloc.aloc.problem.repository.UserCourseProblemRepository;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.mapper.UserMapper;
import com.aloc.aloc.usercourse.entity.UserCourse;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCourseProblemServiceTest {

  @Mock private UserCourseProblemRepository repository;
  @Mock private UserMapper userMapper;
  @InjectMocks private UserCourseProblemService service;

  @Test
  @DisplayName("getTodayProblemId: HIDDEN 상태가 있으면 이전 문제 ID 반환")
  void getTodayProblemIdWhenHiddenPresent() {
    // given
    User user = TestFixture.getMockNewUser();
    UserCourse userCourse = TestFixture.getMockUserCourse(user, CourseType.DAILY, 2, 2);
    Problem problem1 = TestFixture.getMockProblem(101, "문제1", 1);
    Problem problem2 = TestFixture.getMockProblem(102, "문제2", 2);

    UserCourseProblem ucp1 =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem1, UserCourseProblemStatus.UNSOLVED, null, 1);
    UserCourseProblem ucpHidden =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem2, UserCourseProblemStatus.HIDDEN, null, 2);

    // when
    Integer todayProblemId = service.getTodayProblemId(List.of(ucp1, ucpHidden));

    // then
    assertThat(todayProblemId).isEqualTo(problem1.getProblemId());
  }

  @Test
  @DisplayName("getTodayProblemId: HIDDEN 없으면 마지막 문제 ID 반환")
  void getTodayProblemIdWhenNoHidden() {
    // given
    UserCourse userCourse =
        TestFixture.getMockUserCourse(TestFixture.getMockNewUser(), CourseType.DAILY, 2, 2);
    Problem problem1 = TestFixture.getMockProblem(201, "문제1", 2);
    Problem problem2 = TestFixture.getMockProblem(202, "문제2", 3);

    UserCourseProblem ucp1 =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem1, UserCourseProblemStatus.UNSOLVED, null, 1);
    UserCourseProblem ucp2 =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem2, UserCourseProblemStatus.UNSOLVED, null, 2);

    // when
    Integer todayProblemId = service.getTodayProblemId(List.of(ucp1, ucp2));

    // then
    assertThat(todayProblemId).isEqualTo(problem2.getProblemId());
  }

  @Test
  @DisplayName("getSolvedUserCourseProblemByProblem: 결과가 없으면 빈 리스트 반환")
  void getSolvedUserCourseProblemByProblemEmptyResult() {
    // given
    Problem problem = TestFixture.getMockProblem(301, "문제", 3);
    given(
            repository.findAllByProblemAndUserCourseProblemStatusOrderBySolvedAtDesc(
                problem, UserCourseProblemStatus.SOLVED))
        .willReturn(List.of());

    // when
    List<UserCourseProblem> result = service.getSolvedUserCourseProblemByProblem(problem);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("getSolvedUserCourseProblemByProblem: 중복 사용자 제거")
  void getSolvedUserCourseProblemByProblemRemovesDuplicateUsers() {
    // given
    User user = TestFixture.getMockUserByOauthId("User");
    UserCourse uc1 = TestFixture.getMockUserCourse(user, CourseType.DAILY, 2, 2);
    UserCourse uc2 = TestFixture.getMockUserCourse(user, CourseType.DAILY, 2, 2);
    Problem problem = TestFixture.getMockProblem(401, "중복문제", 4);
    LocalDateTime t1 = LocalDateTime.now().minusDays(1);
    LocalDateTime t2 = LocalDateTime.now();

    UserCourseProblem ucp1 =
        TestFixture.getMockUserCourseProblem(uc1, problem, UserCourseProblemStatus.SOLVED, t1, 1);
    UserCourseProblem ucp2 =
        TestFixture.getMockUserCourseProblem(uc2, problem, UserCourseProblemStatus.SOLVED, t2, 2);

    given(
            repository.findAllByProblemAndUserCourseProblemStatusOrderBySolvedAtDesc(
                problem, UserCourseProblemStatus.SOLVED))
        .willReturn(List.of(ucp1, ucp2));

    // when
    List<UserCourseProblem> uniqueSolved = service.getSolvedUserCourseProblemByProblem(problem);

    // then
    assertThat(uniqueSolved).containsExactly(ucp1);
  }

  @Test
  @DisplayName("createUserCourseProblem: 새 엔티티 저장 및 반환")
  void createUserCourseProblemSaveAndReturnEntity() {
    // given
    User user = TestFixture.getMockNewUser();
    UserCourse userCourse = TestFixture.getMockUserCourse(user, CourseType.DAILY, 1, 1);
    Problem problem = TestFixture.getMockProblem(501, "새문제", 5);
    UserCourseProblem saved =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem, UserCourseProblemStatus.HIDDEN, null, 1);

    given(repository.save(any(UserCourseProblem.class))).willReturn(saved);

    // when
    UserCourseProblem result = service.createUserCourseProblem(userCourse, problem, 1);

    // then
    assertThat(result).isEqualTo(saved);
  }

  @Test
  @DisplayName("saveUserCourserProblem: repository.save 위임")
  void saveUserCourserProblemDelegateToRepository() {
    // given
    User user = TestFixture.getMockNewUser();
    UserCourse userCourse = TestFixture.getMockUserCourse(user, CourseType.DAILY, 1, 1);
    Problem problem = TestFixture.getMockProblem(601, "문제", 6);
    UserCourseProblem entity =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem, UserCourseProblemStatus.UNSOLVED, null, 1);

    // when
    service.saveUserCourserProblem(entity);

    // then
    verify(repository).save(entity);
  }

  @Test
  @DisplayName("closeUserCourseProblems: HIDDEN 또는 UNSOLVED 상태만 CLOSED로 변경")
  void closeUserCourseProblemsUpdateOnlyHiddenAndUnsolved() {
    // given
    User user = TestFixture.getMockNewUser();
    UserCourse userCourse = TestFixture.getMockUserCourse(user, CourseType.DAILY, 3, 3);

    UserCourseProblem hiddenProblem =
        TestFixture.getMockUserCourseProblem(
            userCourse,
            TestFixture.getMockProblem(701, "문제A", 7),
            UserCourseProblemStatus.HIDDEN,
            null,
            1);
    UserCourseProblem unsolvedProblem =
        TestFixture.getMockUserCourseProblem(
            userCourse,
            TestFixture.getMockProblem(702, "문제B", 8),
            UserCourseProblemStatus.UNSOLVED,
            null,
            2);
    UserCourseProblem solvedProblem =
        TestFixture.getMockUserCourseProblem(
            userCourse,
            TestFixture.getMockProblem(703, "C", 9),
            UserCourseProblemStatus.SOLVED,
            LocalDateTime.now(),
            3);

    List<UserCourseProblem> all = List.of(hiddenProblem, unsolvedProblem, solvedProblem);

    // when
    service.closeUserCourseProblems(all);

    // then
    assertThat(hiddenProblem.getUserCourseProblemStatus())
        .isEqualTo(UserCourseProblemStatus.CLOSED);
    assertThat(unsolvedProblem.getUserCourseProblemStatus())
        .isEqualTo(UserCourseProblemStatus.CLOSED);
    assertThat(solvedProblem.getUserCourseProblemStatus())
        .isEqualTo(UserCourseProblemStatus.SOLVED);
    verify(repository).saveAll(all);
  }

  @Test
  @DisplayName("getUserCourseProblemByProblem: 존재 시 반환")
  void getUserCourseProblemByProblemWhenFound() {
    // given
    User user = TestFixture.getMockUserByOauthId("User");
    UserCourse userCourse = TestFixture.getMockUserCourse(user, CourseType.DAILY, 1, 1);
    Problem problem = TestFixture.getMockProblem(801, "문제", 8);
    UserCourseProblem expected =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem, UserCourseProblemStatus.UNSOLVED, null, 1);

    given(repository.findByUserAndProblemAndStatus(user, problem, UserCourseProblemStatus.UNSOLVED))
        .willReturn(Optional.of(expected));

    // when
    UserCourseProblem actual = service.getUserCourseProblemByProblem(problem, user);

    // then
    assertThat(actual).isSameAs(expected);
  }

  @Test
  @DisplayName("getUserCourseProblemByProblem: 없으면 예외 발생")
  void getUserCourseProblemByProblemWhenNotFound() {
    // given
    User user = TestFixture.getMockUserByOauthId("User");
    Problem problem = TestFixture.getMockProblem(901, "문제", 9);

    given(repository.findByUserAndProblemAndStatus(user, problem, UserCourseProblemStatus.UNSOLVED))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> service.getUserCourseProblemByProblem(problem, user))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage("이미 해결했거나 도전 중인 문제가 아닙니다.");
  }

  @Test
  @DisplayName("mapToProblemResponseDto: HIDDEN 필터링 후 DTO 매핑")
  void mapToProblemResponseDtoFiltersHiddenAndMapsToDto() {
    // given
    User user = TestFixture.getMockUserByOauthId("User");
    UserCourse userCourse = TestFixture.getMockUserCourse(user, CourseType.DAILY, 3, 3);
    Problem problem = TestFixture.getMockProblem(1001, "문제", 10);

    UserCourseProblem visible =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem, UserCourseProblemStatus.UNSOLVED, null, 1);
    UserCourseProblem hidden =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem, UserCourseProblemStatus.HIDDEN, null, 2);

    LocalDateTime lastSolvedAt = LocalDateTime.of(2025, 7, 14, 12, 0);
    UserCourseProblem history =
        TestFixture.getMockUserCourseProblem(
            userCourse, problem, UserCourseProblemStatus.SOLVED, lastSolvedAt, 3);

    given(
            repository.findAllByProblemAndUserCourseProblemStatusOrderBySolvedAtDesc(
                problem, UserCourseProblemStatus.SOLVED))
        .willReturn(List.of(history));

    UserDetailResponseDto userDto = TestFixture.getMockUserDetailDto(user);
    given(userMapper.mapToUserDetailResponseDto(user)).willReturn(userDto);

    // when
    List<ProblemResponseDto> dtos = service.mapToProblemResponseDto(List.of(visible, hidden));

    // then
    assertThat(dtos).hasSize(1);
    ProblemResponseDto dto = dtos.get(0);
    assertThat(dto.getProblemId()).isEqualTo(problem.getProblemId());
    assertThat(dto.getTitle()).isEqualTo(problem.getTitle());
    assertThat(dto.getRank()).isEqualTo(problem.getRank());
    assertThat(dto.getStatus()).isEqualTo(UserCourseProblemStatus.UNSOLVED);
    assertThat(dto.getLastSolvedAt()).isEqualTo(lastSolvedAt);
    assertThat(dto.getSolvingUserNum()).isEqualTo(1);
    assertThat(dto.getSolvingUserList()).containsExactly(userDto);
  }

  @Test
  @DisplayName("getByUserCourse: repository 호출 위임")
  void getByUserCourseDelegateToRepository() {
    // given
    UserCourse userCourse =
        TestFixture.getMockUserCourse(TestFixture.getMockNewUser(), CourseType.DAILY, 1, 1);
    UserCourseProblem ucp =
        TestFixture.getMockUserCourseProblem(
            userCourse,
            TestFixture.getMockProblem(1101, "문제", 11),
            UserCourseProblemStatus.UNSOLVED,
            null,
            1);
    List<UserCourseProblem> expected = List.of(ucp);

    given(repository.findAllByUserCourseOrderByProblemOrderAsc(userCourse)).willReturn(expected);

    // when
    List<UserCourseProblem> actual = service.getByUserCourse(userCourse);

    // then
    assertThat(actual).isEqualTo(expected);
  }
}
